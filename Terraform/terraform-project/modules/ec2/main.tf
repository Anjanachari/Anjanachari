resource "aws_instance" "web" {
  ami               = var.ami_id
  instance_type     = var.instance_type
  subnet_id         = var.subnet_id  # The subnet ID links the instance to the correct VPC

  vpc_security_group_ids = [var.security_group_id]  
  associate_public_ip_address = true
  user_data = <<-EOF
              #!/bin/bash
              yum update -y                    # Update the system
              yum install -y httpd             # Install Apache HTTP Server
              systemctl start httpd            # Start Apache service
              systemctl enable httpd           # Enable Apache to start on boot
              echo "Hello, World!" > /var/www/html/index.html  # Create a basic index page
              EOF
# Attach the security group to the instance

  tags = {
    Name = "MyWebServer"
  }
}

