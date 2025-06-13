resource "aws_instance" "instance1" {
    ami = var.image_id
    instance_type = var.instance_type
    user_data = var.user_data
    vpc_security_group_ids = var.security_group_ids
    tags = {
        Name = "anjana"
    }
}