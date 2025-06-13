output "security_group_ids" {
  description = "The security group IDs associated with the EC2 instance"
  value       = aws_instance.instance1.security_groups
}
