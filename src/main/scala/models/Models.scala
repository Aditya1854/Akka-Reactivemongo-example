package models

case class Student(_id:Int, name:String, email:String, universityId:Int,DOB:String)
//{"_id":1,"name":"Aditya kumar","email":"aditya@gmail.com","universityId":1,"DOB":"1996-05-06"}
case class University(_id:Int, name:String, location:String)

case class StudentJoin(_id:Int,name:String,email:String,universityId:Int,DOB:String,universityDetails:Array[University])

case class UniversityJoin(_id:Int, name:String, location:String,students:Array[Student])

case class UniversityData(_id:Int,name:String,location:String,counts:Int)

case class StudentData(_id:Int,name:String,email:String,universityName :String,DOB:String)

case class User(firstName : String,lastName : String, email :String,password:String)

case class UserData(email:String,password:String)

case class UserName(firstName:String,email:String)