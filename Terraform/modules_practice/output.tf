output "vpc_id" {
  description = "The ID of the VPC"
  value       = module.vpc.vpc_id
}

output "security_group_id" {
  description = "The security group ID"
  value       = module.security_group.security_group_id
}



output "ec2_security_group_ids" {
  description = "The security group IDs associated with the EC2 instance"
  value       = module.ec2.security_group_ids  # This correctly references the EC2 output
}
