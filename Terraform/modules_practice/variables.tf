variable "image_id" {
  description = "AMi id"
  type        = string
  default     = "ami-0100e595e1cc1ff7f"
}

variable "instance_type" {
  description = "Instance type"
  type        = string
  default     = "t2.micro"
}

variable "user_data" {
  description = "Users data manual"
  type        = string
  default     = <<-EOF
              #!/bin/bash
              sudo yum update -y
              sudo yum install -y httpd
              sudo systemctl start httpd
              sudo systemctl enable httpd
              sudo bash -c 'echo "Welcome to My Website - Anjana" > /var/www/html/index.html'
              EOF
}

variable "cidr_block" {
  description = "CIDR block for the VPC"
  type        = string
  default     = "10.0.0.0/16"
}