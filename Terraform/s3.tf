provider "aws" {
  region = "us-east-2"
}

# Get the available availability zones dynamically
data "aws_availability_zones" "available" {}

resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"
  tags = {
    Name = "MyCustomVPC"
  }
}

# Create Subnet 1 in Availability Zone 1
resource "aws_subnet" "subnet_1" {
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.1.0/24"
  availability_zone = data.aws_availability_zones.available.names[0]  # AZ 1
  tags = {
    Name = "MyCustomSubnet1"
  }
}

# Create Subnet 2 in Availability Zone 2
resource "aws_subnet" "subnet_2" {
  vpc_id     = aws_vpc.main.id
  cidr_block = "10.0.2.0/24"
  availability_zone = data.aws_availability_zones.available.names[1]  # AZ 2
  tags = {
    Name = "MyCustomSubnet2"
  }
}

resource "aws_security_group" "sg1" {
  name        = "ansg1"
  description = "ansg1"
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  ingress {
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = -1
    cidr_blocks = ["0.0.0.0/0"]
  }
}

# Create an Internet Gateway
resource "aws_internet_gateway" "gw" {
  vpc_id = aws_vpc.main.id
  tags = {
    Name = "MyCustomInternetGateway"
  }
}

# Create a Route Table
resource "aws_route_table" "main" {
  vpc_id = aws_vpc.main.id

  # Define a route to the Internet Gateway (for public access)
  route {
    cidr_block = "0.0.0.0/0"
    gateway_id = aws_internet_gateway.gw.id
  }

  tags = {
    Name = "MyCustomRouteTable"
  }
}

# Associate the Route Table with the Subnets
resource "aws_route_table_association" "subnet_association_1" {
  subnet_id      = aws_subnet.subnet_1.id
  route_table_id = aws_route_table.main.id
}

resource "aws_route_table_association" "subnet_association_2" {
  subnet_id      = aws_subnet.subnet_2.id
  route_table_id = aws_route_table.main.id
}

# Create an EC2 instance
resource "aws_instance" "instance1" {
  ami             = "ami-0100e595e1cc1ff7f"
  instance_type   = "t2.micro"
  security_groups = [aws_security_group.sg1.name]
  user_data = <<-EOF
              #!/bin/bash
              sudo yum update -y
              sudo yum install -y httpd
              sudo systemctl start httpd
              sudo systemctl enable httpd
              sudo bash -c 'echo "Welcome to My Website - " > /var/www/html/index.html'
              EOF
  tags = {
    Name = "anjan-instance-1"
  }
}

# Dynamically create an S3 bucket with a name based on the current date
resource "aws_s3_bucket" "my_bucket" {
  bucket = "my-unique-bucket-name-${formatdate("YYYY-MM-DD", timestamp())}"

  tags = {
    Name        = "MyS3Bucket"
    Environment = "Dev"
  }
}

# Create EBS volume in the same availability zone as the EC2 instance
resource "aws_ebs_volume" "ebs_volume" {
  availability_zone = "us-east-2a"
  size              = 2
  type              = "gp3"
  encrypted         = true
  tags = {
    Name = "MyEBSVolume"
  }
}

# Attach EBS volume to EC2 instance
resource "aws_volume_attachment" "attachment" {
  device_name = "/dev/sdh" 
  volume_id   = aws_ebs_volume.ebs_volume.id
  instance_id = aws_instance.instance1.id
}

# Create a Load Balancer
resource "aws_lb" "my_lb" {
  name               = "my-load-balancer"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [aws_security_group.sg1.id]
  subnets            = [aws_subnet.subnet_1.id, aws_subnet.subnet_2.id]  # Use both subnets in different AZs

  enable_deletion_protection = false

  tags = {
    Name = "MyLoadBalancer"
  }
}

# Create a Load Balancer Target Group
resource "aws_lb_target_group" "my_target_group" {
  name     = "my-target-group"
  port     = 80
  protocol = "HTTP"
  vpc_id   = aws_vpc.main.id

  health_check {
    path                = "/"
    interval            = 30
    timeout             = 5
    healthy_threshold   = 3
    unhealthy_threshold = 3
  }

  tags = {
    Name = "MyTargetGroup"
  }
}

# Register EC2 instance with the Target Group
resource "aws_lb_target_group_attachment" "my_target_group_attachment" {
  target_group_arn = aws_lb_target_group.my_target_group.arn
  target_id        = aws_instance.instance1.id
  port             = 80
}

# Create a Load Balancer Listener
resource "aws_lb_listener" "my_listener" {
  load_balancer_arn = aws_lb.my_lb.arn
  port              = 80
  protocol          = "HTTP"

  default_action {
    type = "fixed-response"
    fixed_response {
      status_code  = 200
      content_type = "text/plain"
      message_body = "Welcome to My Load Balanced Website"
    }
  }
}

# Monitoring: Create a CloudWatch Alarm for CPU Utilization
resource "aws_cloudwatch_metric_alarm" "high_cpu_alarm" {
  alarm_name                = "high-cpu-alarm"
  comparison_operator       = "GreaterThanOrEqualToThreshold"
  evaluation_periods        = "1"
  metric_name               = "CPUUtilization"
  namespace                 = "AWS/EC2"
  period                    = "300"
  statistic                 = "Average"
  threshold                 = "80"
  alarm_description         = "This alarm triggers if CPU usage exceeds 80% for 5 minutes."
  insufficient_data_actions = []
  alarm_actions             = []

  dimensions = {
    InstanceId = aws_instance.instance1.id
  }
}

# Combined output for both EC2 public IP, S3 bucket name, EBS volume ID, Load Balancer DNS
output "combined_output" {
  value = {
    instance_public_ip = aws_instance.instance1.public_ip
    s3_bucket_name     = aws_s3_bucket.my_bucket.bucket
    ebs_volume_id      = aws_ebs_volume.ebs_volume.id
    ebs_volume         = aws_ebs_volume.ebs_volume.availability_zone
    load_balancer_dns  = aws_lb.my_lb.dns_name
  }
}
