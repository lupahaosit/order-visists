package com.example.schoolmarksproject.Models

class Mark {
    var name: String = ""
    var mark: Int = 0
    var email: String = ""

    public constructor( name : String, mark : Int, email : String){
        this.name = name;
        this.mark = mark;
        this.email = email
    }
    public constructor()

    fun copy(mark: Int = this.mark) = Mark(name, mark, email)
}