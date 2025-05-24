package com.example.schoolmarksproject.Models

import com.example.schoolmarksproject.enums.roles

class User {
    var id : Int = 0
    var name : String? = null
    var surname : String? = null
    var role : String? = ""
    var email : String? = null
    var classNumber : String? = null

    public constructor(name : String, surname : String, role : String, email : String){
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.email = email;
    }

    public constructor(name : String, surname : String, role : String, email : String, classNumber : String){
        this.name = name;
        this.surname = surname;
        this.role = role;
        this.email = email;
        this.classNumber = classNumber
    }

    public constructor()



}