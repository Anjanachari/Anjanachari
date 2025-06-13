resource "aws_vpc" "anjana_vpc" {
    cidr_block = var.cidr_block
    tags = {
      Name =  "Anjana-vpc"
    }
  
}
resource "aws_subnet" "anjana_subnet" {
    cidr_block = var.cidr_block
    vpc_id = aws_vpc.anjana_vpc.id
    tags = {
      Name = "anjana-subnet"
    }
  
}