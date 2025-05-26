package com.example.schoolmarksproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.CardDefaults.cardElevation
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.schoolmarksproject.Models.Visit
import com.example.schoolmarksproject.Models.User
import com.example.schoolmarksproject.Models.UserVisit
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.format.TextStyle
import java.util.Calendar
import kotlin.jvm.java
import kotlin.text.split


class MainActivity : ComponentActivity() {

    //region variables standart
    private lateinit var navController: NavHostController
    private val months = listOf(
        "январь", "февраль", "март", "апрель",
        "май", "июнь", "июль", "август",
        "сентябрь", "октябрь", "ноябрь", "декабрь"
    )
    val monthsWithDays = mapOf(
        "январь" to 31,
        "февраль" to 28, // 29 в високосный год
        "март" to 31,
        "апрель" to 30,
        "май" to 31,
        "июнь" to 30,
        "июль" to 31,
        "август" to 31,
        "сентябрь" to 30,
        "октябрь" to 31,
        "ноябрь" to 30,
        "декабрь" to 31
    )
    val semestrStartMonth = "сентябрь"
    val semestrEndMonth = "декабрь"
    val semestrEndDay = 31
    val semestrStartDay = 1
    private var auth = Firebase.auth
    private var roles = listOf("Ученик", "Учитель")
    private var subjects = listOf("Элементы высшей математики",
            "Основы проектирования базы данных",
            "МДК",
            "Безопасность жизнедеятельности",
            "Современные языки программирования",
            "Физическая культура",
            "Иностранный язык в профессиональной деятельности",
            "Архитектура аппаратных средств",
            "Численные методы")
    var directory = mapOf(
        "Вопрос: Как зарегистрироваться в системе?" to " Ответ: Для регистрации необходимо заполнить форму с личными данными",
        "Вопрос: Как просмотреть свою посещаемость?" to "Ответ: Выберите месяц и предмет, после чего вы увидите все записи о посещаемости.",
        "Вопрос: Что делать, если я пропустил занятие?" to "Ответ: Вы можете связаться с преподавателем для получения материалов занятия и отметки о пропуске.",
        "Вопрос: Как обжаловать отметку о посещаемости?" to "Ответ: Свяжитесь с преподавателем через систему или по электронной почте для обсуждения вопроса.",
        "Вопрос: Могу ли я видеть посещаемость других студентов?" to " Ответ: Нет, доступ к посещаемости других студентов ограничен для защиты конфиденциальности.",
        "Вопрос: Что делать, если я заметил ошибку в своей посещаемости?" to " Ответ: Сообщите об этом преподавателю или администратору системы для исправления.",
       )
    private lateinit var currentUser : User
    private lateinit var user : FirebaseUser

    //endregion

    //region mutablesVariables

    private var email = mutableStateOf<String>("")
    private var password = mutableStateOf<String>("")
    private var name = mutableStateOf<String>("")
    private var surname = mutableStateOf<String>("")
    private var classNumber = mutableStateOf("")
    private var role = mutableStateOf("")
    private var classList = mutableListOf<String>()
    private var chosenClass = mutableStateOf("")
    private var chosenMonth = mutableStateOf("")
    private var chosenDay = mutableStateOf("")
    private var chosenSubject = mutableStateOf("")
    private var usersList = mutableListOf<User>()
    private var classUserList = mutableStateListOf<User>()
    private var marksListOfTheClasses = mutableStateListOf<Visit>()
    private var marksListOfTheUser = mutableStateListOf<Visit>()
    private var isDataLoaded = mutableStateOf(false)
    private var IsneedClassChoose = mutableStateOf(false)
    private var subjectList = mutableListOf<String>()
    //endregion

    //region standard functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //logOut()
        var user = Firebase.auth.currentUser
        chosenMonth.value = months[0]
        chosenSubject.value = subjects[0]
        lifecycleScope.launch {
            try {
                usersList.addAll(getAllUsers("Visits"))
                if (user != null){
                    currentUser = usersList.firstOrNull { it.email == user?.email }!!
                    if (currentUser.role == "Ученик"){
                        if (currentUser.classNumber == ""){
                            IsneedClassChoose.value = true

                        }else{
                            chosenClass.value = currentUser.classNumber!!

                        }
                    }
                }
                getAllClasses("Visits")
                isDataLoaded.value = true
            } catch (e: Exception) {
                Log.e("Firebase", "Ошибка получения данных: ${e.message}")
            }
        }
        setContent {
            if (isDataLoaded.value){
                navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = if (user == null) "login" else "monthsGrid"
                ) {
                    composable("login") {
                        Column {
                            header()
                            loginPageVisits()
                        }
                        //SimpleAttendancePage()
                        //SubjectGradesPage()
                        //saveMarks()
                        //loginPageMarks()

                    }

                    composable("monthsGrid") {
                        if (currentUser.role == "Учитель"){
                            Column {
                                header()
                                ChooseVisitsDataPage()
                            }

                        }else{
                            Column {
                                header()
                                SimpleAttendancePage()
                            }

                            //MonthPage()

                        }
                    }
                    composable("visitsMainPage") {
                        SimpleAttendancePage()
                    }
                    composable ("teacherVisitsPage"){
                        ChooseVisitsDataPage()
                    }
                    composable("visitsPage"){
                        VisitsPage()
                    }
                    composable("register") {
                        //registerPageMarks()
                        Column {
                            header()
                            registerPageVisits()
                        }
                    }
                    composable("directory"){
                        Column {  header()
                            DirectoryPage()
                        }
                    }
                    composable ("settingsPage"){
                        Column{
                            header()
                            SettingsPage()
                        }
                    }
                    composable ("notificationsDescription/{subjectNames}"){ inputSubjects ->
                        val problemSubjects = inputSubjects.arguments
                        var sub =  problemSubjects?.getString("subjectNames")?.split(';')?.toList()!!
                        Column{
                            header()
                            NotificationsDescription(sub)
                        }
                    }
                }
            }else{
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
        }
    }

    //Получение всех классов
    private suspend fun getAllClasses(classesDivision : String) : List<String>{
        val database = Firebase.database.reference
        val dataRef = database.child(classesDivision).child("Classes")
        val snapshot = dataRef.get().await()

        return snapshot.children.map { it.value.toString() }
    }

    //Получение всех пользователй(учителей и учеников)
    suspend fun getAllUsers(classesDivision: String): List<User> {
        val users = mutableListOf<User>()
        val database = Firebase.database.reference
        val dataRef = database.child(classesDivision).child("Users")

        val snapshot = dataRef.get().await() // await вместо addOnSuccessListener

        snapshot.children.forEach { user ->
            val name = user.child("name").getValue<String>() ?: ""
            val surname = user.child("surname").getValue<String>() ?: ""
            val email = user.child("email").getValue<String>() ?: ""
            val role = user.child("role").getValue<String>() ?: ""

            if (role == "Ученик") {
                val classNumber = user.child("classNumber").getValue<String>() ?: ""
                users.add(User(name, surname, role, email, classNumber))
            } else {
                users.add(User(name, surname, role, email))
            }
        }

        return users
    }

    //Получает все учебные предметы
    private suspend fun getAllSubjects() : List<String>{
        var database = Firebase.database.reference
        var dataRef = database.child("Visits").child("Subjects")
        var snapshot = dataRef.get().await()
        var subjects = snapshot.children.map { it.key.toString() }

        return subjects

    }

    //Создаётся класс и к нему добавляются все учебные предметы
    private suspend fun createClassAndAdd(className : String){
        var subjects = getAllSubjects()
        var database = Firebase.database.reference.child("Visits")

        database.child("Classes").child(className).setValue(className)
        subjects.forEach {subject ->
            database.child("Visits").child(className).child(subject).setValue("")
        }
    }

    //получаем все посещения по классу
    private suspend fun getClassVisits() : List<Visit>{
        var database = Firebase.database.reference
        var dataRef = database.child("Visits").child("Visits").child(chosenClass.value)
            .child(chosenSubject.value).child(chosenMonth.value).child(chosenDay.value)
        var snapshot = dataRef.get().await()
        var visits = snapshot.children.map {
            Visit(it.child("email").value.toString(), it.child("visit").value.toString(), it.child("name").value.toString())
        }.toList()
        return visits
    }

    //получение всех учеников класса
    private suspend fun getAllClassUsers() : List<User>{
        var database = Firebase.database.reference
        var dataRef = database.child("Visits").child("Users")

        var snapshot = dataRef.get().await()
        var classNumber = snapshot.children.map { student ->
            student.child("classNumber")}
        var students = snapshot.children.filter { student ->
            student.child("classNumber").value.toString() == chosenClass.value }
            .map{ User(name = it.child("name").value.toString(), surname = it.child("surname").value.toString(), role = it.child("role").value.toString(), email = it.child("email").value.toString()) }

        return students
    }

    //получение студентво и их оценок, на пропусках '-'
    suspend fun getClassVisitsJoinedWithUsers(): List<Visit> {
        val database = Firebase.database.reference

        // 1. Получаем список всех учеников класса
        val usersRef = database.child("Visits").child("Users")
        val usersSnapshot = usersRef.get().await()
        val students = usersSnapshot.children
            .filter { it.child("classNumber").value?.toString() == chosenClass.value }
            .map {
                User(
                    name = it.child("name").value.toString(),
                    surname = it.child("surname").value.toString(),
                    role = it.child("role").value.toString(),
                    email = it.child("email").value.toString()
                )
            }

        // 2. Получаем список всех посещений на дату
        val visitsRef = database.child("Visits").child("Visits")
            .child(chosenClass.value)
            .child(chosenSubject.value)
            .child(chosenMonth.value)
            .child(chosenDay.value)

        val visitsSnapshot = visitsRef.get().await()
        val visits = visitsSnapshot.children.map {
            Visit(
                name = it.child("name").value.toString(),
                email = it.child("email").value.toString(),
                visit = it.child("visit").value.toString(),

            )
        }

        // 3. Соединяем: по каждому студенту ищем его посещение по email
        return students.map { student ->
            val visitRecord = visits.find { it.email == student.email }
            Visit(
                name = "${student.name!!} ${student.surname!!}",
                email = student.email!!,
                visit = visitRecord?.visit ?: "—"
            )
        }
    }

    //Проверка значения поля учителя
    private fun checkVisitInputValue(visitStatus : String) : Boolean{
        var allowSymbols = ArrayList<Char>()
        allowSymbols.add('-')
        allowSymbols.add('P')
        allowSymbols.add('N')
        allowSymbols.add('Н')
        allowSymbols.add('П')

        if (visitStatus.length > 1){return false}
        if (visitStatus == "") return true
        return (allowSymbols.contains(visitStatus[0].uppercaseChar()))
    }

    //сохранения статуса посещения студентами
    private fun saveVisitClassStatus(visits : List<Visit>) {
        var database = Firebase.database.reference
        var dataRef = database.child("Visits").child("Visits").child(chosenClass.value)
            .child(chosenSubject.value).child(chosenMonth.value).child(chosenDay.value)
        visits.forEach {visit ->
            dataRef.child(visit.email.substringBefore('.')).setValue(visit)
        }
    }

    //добавление пользователя в бд
    private fun saveUserToFirebase(email : String, role : String, name : String, surname : String, className : String = ""){
        var database = Firebase.database.reference
        var dataRef = database.child("Visits").child("Users").child(email.split('.')[0])

        dataRef.setValue(User(name, surname, role, email, className))
    }

    //проверка, есть ли такой класс
    private suspend fun checkClass(className : String) : Boolean{
        var database = Firebase.database.reference
        var dataRef = database.child("Visits").child("Classes")
        var snapshot = dataRef.get().await()
        var isClassExist=  snapshot.children.map { it.value }.contains(className)
        return isClassExist
    }

    private suspend fun getStudentsVisitsPerMonth(){
        var database = Firebase.database.reference
        var dataRef = database.child("Visits").child("Visits")
            .child(currentUser.classNumber!!).child(chosenSubject.value).child(chosenMonth.value)

        var snapshot = dataRef.get().await()
    }

    private fun login() {
        if (email.value.isEmpty() || password.value.isEmpty()) {
            Toast.makeText(
                baseContext,
                "Заполните все поля",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        auth.signInWithEmailAndPassword(email.value, password.value.toString())
            .addOnSuccessListener {
                user = Firebase.auth.currentUser!!
                var isUserExist = usersList.filter { it -> it.email == user.email }.size != 0
                if (isUserExist){
                    currentUser = usersList.first { it -> it.email == user.email }
                }else{
                    logOut()
                    Toast.makeText(
                        baseContext,
                        "Неверные почта или пароль",
                        Toast.LENGTH_SHORT,
                    ).show()
                    return@addOnSuccessListener
                }

                if (currentUser.role == "Ученик"){
                    chosenClass.value = currentUser.classNumber!!
                }
                navController.navigate("monthsGrid")

            }
            .addOnFailureListener {
                Toast.makeText(
                    baseContext,
                    "Неверные почта или пароль",
                    Toast.LENGTH_SHORT,
                ).show()
            }
    }

    private suspend fun registerVisits(){
        var isClassExist = false
        if (email.value.isEmpty() || password.value.isEmpty() || name.value.isEmpty()
            || surname.value.isEmpty()){
            Toast.makeText(
                baseContext,
                "Заполните все поля",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        if (role.value == ""){
            Toast.makeText(
                baseContext,
                "Выберите роль",
                Toast.LENGTH_SHORT,
            ).show()
            return
        }
        if (role.value == "Ученик"){
            isClassExist = checkClass(chosenClass.value)
            classNumber.value = chosenClass.value
            if (!isClassExist){
                Toast.makeText(
                    baseContext,
                    "Данный класс не существует",
                    Toast.LENGTH_SHORT,
                ).show()
                return
            }
        }

        auth.createUserWithEmailAndPassword(email.value, password.value.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {

                    user = auth.currentUser!!
                    if (role.value == "Ученик"){
                        navController.navigate("visitsMainPage")
                        saveUserToFirebase(email.value, role.value, name.value, surname.value, chosenClass.value)
                    }else{
                        navController.navigate("teacherVisitsPage")
                        saveUserToFirebase(email.value, role.value, name.value, surname.value )
                    }
                    currentUser = User(name.value, surname.value, email = email.value, role = role.value)
                    usersList.add(currentUser)


                } else {

                    Toast.makeText(
                        baseContext,
                        "Autentifiation failed.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    private fun logOut() {
        Firebase.auth.signOut()
    }

    private suspend fun getAttendanceListForMonth (): Map<Int, String?> {
        var database = Firebase.database.reference
        val attendanceList = mutableMapOf<Int, String?>()
        var dataRef = database.child("Visits").child("Visits").child(currentUser.classNumber!!)
            .child(chosenSubject.value).child(chosenMonth.value)
        var snapshot = dataRef.get().await()
        var days = monthsWithDays[chosenMonth.value]
        for (day in 1..days!!){
            var daySnapShot = snapshot.child(day.toString())
            if (daySnapShot.child(currentUser.email!!.substringBefore('.')).exists()){
                var data = snapshot.child(day.toString()).child(currentUser.email!!.substringBefore('.'))
                attendanceList.put(day, data.child("visit").value.toString())
            }
            else{
                attendanceList.put(day, null)
            }
        }
        return attendanceList
    }


    //region Composable Elements Visits
    @Composable
    private fun loginPageVisits() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Логотип или заголовок
                Icon(
                    imageVector = Icons.Filled.Lock,
                    contentDescription = "Login",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Заголовок
                Text(
                    text = "Добро пожаловать",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Поле email
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Почта") },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = "Email")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),

                    )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле пароля
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Пароль") },
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = "Password")
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),

                    )

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка входа
                Button(
                    onClick = { login() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text("Войти", style = MaterialTheme.typography.labelLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Альтернативный вариант входа
                Text(
                    text = "или",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка регистрации
                OutlinedButton(
                    onClick = { navController.navigate("register") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Создать аккаунт", style = MaterialTheme.typography.labelLarge)
                }

            }
        }
    }

    @Composable
    private fun registerPageVisits(){
        var isUserStudent by remember{ mutableStateOf(true)}
        role.value = "Ученик"


        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Заголовок
                Text(
                    text = "Регистрация",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 32.dp)
                )

                // Поле email
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Почта") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле пароля
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Пароль") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Поле имени
                OutlinedTextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Имя") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = surname.value,
                    onValueChange = { surname.value = it },
                    label = { Text("Фамилия") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row{
                    Checkbox(
                        checked = isUserStudent,
                        onCheckedChange = {
                            isUserStudent = !isUserStudent
                            if (isUserStudent){
                                role.value = "Ученик"
                            }
                            else{
                                role.value = "Учитель"
                            }
                        }
                    )
                    Text("Вы ученик?", modifier = Modifier.padding(top = 16.dp))
                }


                if (isUserStudent){
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = chosenClass.value,
                        onValueChange = { chosenClass.value = it },
                        label = { Text("Класс") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }



                Spacer(modifier = Modifier.height(32.dp))

                // Кнопка регистрации
                FilledTonalButton(
                    onClick = { lifecycleScope.launch {
                        registerVisits()
                    }},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Зарегистрироваться")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Разделитель
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                    Text(
                        text = "или",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Divider(
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Кнопка входа
                OutlinedButton(
                    onClick = { navController.navigate("login") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Уже есть аккаунт? Войти")
                }
            }
        }

    }
    //endregion

    @Composable
    fun classListPage(){
        var IsNewClassCreating by remember {mutableStateOf(false)}
        var className by remember {mutableStateOf("")}
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // мягкий светлый фон
        ) {
            Text(
                text = "Классы",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(16.dp),
                color = Color(0xFF3F51B5), // синий заголовок
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(classList) { classItem ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                chosenClass.value = classItem
                                navController.navigate("subjectChoosePage")
                            },
                        colors = cardColors(containerColor = Color.White),
                        elevation = cardElevation(defaultElevation = 4.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Text(
                                text = classItem,
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Row{
                Button(onClick = {
                    if (IsNewClassCreating){
                        IsNewClassCreating = false
                        lifecycleScope.launch {
                            createClassAndAdd(className)
                        }
                    }else{
                        IsNewClassCreating = true
                    }
                }) {
                    Text("Создать новый класс")
                }
                if (IsNewClassCreating){
                    TextField(value = className, onValueChange = {className = it}, placeholder = {Text("Название класса") } )
                }
            }

        }
    }

    @Composable
    fun createSelect(title : String, itemsList : List<String>, targetValue : MutableState<String>){
        var expandedValues by remember {mutableStateOf(false);}
        var itemValue by remember {mutableStateOf(title)}

        Row (
            modifier = Modifier.clickable {
                expandedValues = !expandedValues
            },
        ){
            Text(text = itemValue)
            Spacer(modifier = Modifier.height(50.dp))
            Icon(imageVector = Icons.Filled.ArrowDropDown, "downList")
            DropdownMenu(
                expanded = expandedValues,
                onDismissRequest = { expandedValues = false }
            ) {
                itemsList.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item) },
                        onClick = {
                            expandedValues = false
                            targetValue.value = item
                            itemValue = item
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun SubjectListPage() {
        var subjects by remember { mutableStateOf<List<String>>(emptyList()) }

        LaunchedEffect(Unit) {
            subjects = getAllSubjects()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Text(
                text = "Предметы",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF3F51B5),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                items(subjects) { subject ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable {
                                chosenSubject.value = subject
                                navController.navigate("monthsPage")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = cardColors(containerColor = Color.White),
                        elevation = cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(start = 20.dp)
                        ) {
                            Text(
                                text = subject,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun NotificationsDescription(subjectNames: List<String>) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (subjectNames.isEmpty()) {
                Text(
                    text = "Нет проблемных предметов!",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Green
                )
            } else {
                Text(
                    text = "Внимание: проблемные предметы",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )

                Divider(color = Color.LightGray, thickness = 1.dp)

                subjectNames.forEach { subject ->
                    if (subject != "") {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFFFA000),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = subject,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "средний балл ниже 3.5",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                }
            }
        }
    }

    @Composable
    private fun header() {
        val backgroundColor = Color(0xFF3F51B5) // Тёмно-синий
        val textColor = Color.White

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(backgroundColor)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                var user = Firebase.auth.currentUser
                if (user != null) {
                    if (currentUser.role == "Ученик") {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = "Оценки",
                                tint = textColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Посещаемость",
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.clickable { navController.navigate("monthsGrid") }
                            )
                        }
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = "Дневник",
                                tint = textColor,
                                modifier = Modifier
                                    .size(20.dp)
                                    .clickable { navController.navigate("subjectGradesPage") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Посещаемость",
                                Modifier.clickable { navController.navigate("monthsGrid") },
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }

                }else{
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Войти",
                            Modifier.clickable { navController.navigate("login") },
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Divider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Справочник",
                        tint = textColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Справочник",
                        Modifier.clickable{navController.navigate("directory")},
                        color = textColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Divider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .height(24.dp)
                            .width(1.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Настройки",
                        tint = textColor,
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { navController.navigate("settingsPage") }
                    )
                }
            }
        }
    }

    @Composable
    private fun SettingsPage(){
        Box(Modifier.fillMaxSize()){
            Column ( horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)){
                Button(onClick = {
                    logOut()
                    navController.navigate("login")
                }) {
                    Text("Выйти")
                }
            }
        }
    }

    @Composable
    fun BarChart(data: Map<Int, Int>) {
        val maxCount = (data.values.maxOrNull() ?: 1).toFloat()
        val barColors = listOf(Color(0xFF4CAF50), Color(0xFFFFC107), Color(0xFFF44336), Color(0xFF2196F3), Color(0xFF9C27B0))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            data.toSortedMap().entries.toList().forEachIndexed { index, entry ->
                val (mark, count) = entry
                val barHeightRatio = count / maxCount
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Box(
                        modifier = Modifier
                            .height((150 * barHeightRatio).dp)
                            .width(30.dp)
                            .background(barColors[index % barColors.size])
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(mark.toString(), fontWeight = FontWeight.Bold)
                    Text("$count", fontSize = 12.sp)
                }
            }
        }
    }


    @Composable
    private fun DirectoryPage(){
        val expandedItems = remember { mutableStateMapOf<String, Boolean>() }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            directory.forEach { (question, answer) ->
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                expandedItems[question] = !(expandedItems[question] ?: false)
                            },
                        elevation = cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .animateContentSize() // плавное раскрытие
                        ) {
                            Text(
                                text = question,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (expandedItems[question] == true) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = answer,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun VisitsPage() {
        val date = "${chosenMonth.value}: ${chosenDay.value}"
        val subject = chosenSubject.value

        var users by remember { mutableStateOf(emptyList<Visit>()) }

        LaunchedEffect(Unit) {
            users = getClassVisitsJoinedWithUsers()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Row(){
                // Шапка
                Text(date, fontSize = 14.sp, color = Color.Gray)
                Text(subject, fontSize = 20.sp, fontWeight = FontWeight.Bold)

            }

            users.forEach { user ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Имя и фамилия (строго слева)
                    Text(
                        text = "${user.name}",
                        modifier = Modifier.weight(1f)
                    )

                    // TextField для посещения (строго справа)
                    TextField(
                        value = user.visit,
                        onValueChange = {
                            if(checkVisitInputValue(it)){
                                user.visit = it.uppercase()
                            } },  // Логика не меняется, оставляем пустым
                        modifier = Modifier.width(120.dp)
                    )
                }
            }
            Button(onClick = {
                saveVisitClassStatus(users)
                navController.navigate("monthsGrid")
            }) {
                Text("Обновить")
            }
        }
    }

    //Добавить стили
    @Composable
    fun SimpleAttendancePage() {
        var dataVisit by remember { mutableStateOf<Map<Int, String?>>(emptyMap()) }
        var visitsStat by remember { mutableStateOf<Map<String?, List<String?>>>(emptyMap()) }

        LaunchedEffect(chosenMonth.value, chosenSubject.value) {
            dataVisit = getAttendanceListForMonth()
            visitsStat = dataVisit.values.filter { visit -> visit != null }.groupBy { it }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text("Месяц", style = MaterialTheme.typography.titleMedium)
            createSelect(months[0], months, chosenMonth)
            Spacer(modifier = Modifier.height(12.dp))

            Text("Предмет", style = MaterialTheme.typography.titleMedium)
            createSelect(subjects[0], subjects, chosenSubject)
            Spacer(modifier = Modifier.height(24.dp))

            if(visitsStat["Н"]?.let { it.size == 1 } == true){
                Text("У вас проблема с предметом, ваша посещаемость слишком низка, вы отсутвовали на ${visitsStat["P"]?.size} занятий!", color = Color.Red)
            }
            AttendanceBarChart(visitsStat)

            Text("Посещаемость", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(dataVisit.toList()) { (day, visit) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("День $day", style = MaterialTheme.typography.bodyLarge)
                        Text(
                            text = visit ?: "-",
                            style = MaterialTheme.typography.bodyLarge,
                            color = when (visit) {
                                "присутствовал" -> Color(0xFF2E7D32) // Зеленый
                                "отсутствовал" -> Color(0xFFC62828) // Красный
                                else -> Color.Gray
                            }
                        )
                    }
                    Divider()
                }
            }
        }
    }

    @Composable
    fun AttendanceBarChart(visitsStat: Map<String?, List<String?>>) {
        val presenceCount = visitsStat["П"]?.size ?: 0
        val absenceCount = visitsStat["Н"]?.size ?: 0
        val total = presenceCount + absenceCount

        if (total == 0) {
            Text("Нет данных о посещениях", modifier = Modifier.padding(16.dp))
            return
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок
            Text(
                text = "Статистика посещений",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Полоса статистики
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.LightGray)
            ) {
                // Часть присутствий (только если есть присутствия)
                if (presenceCount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(presenceCount.toFloat())
                            .background(Color.Green)
                    )
                }

                // Часть отсутствий (только если есть отсутствия)
                if (absenceCount > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(absenceCount.toFloat())
                            .background(Color.Red)
                    )
                }

                // Специальный случай, когда только один тип данных
                if (presenceCount == 0 || absenceCount == 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(if (presenceCount > 0) Color.Green else Color.Red)
                    )
                }
            }

            // Легенда
            Row(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (presenceCount > 0) {
                    LegendItem(Color.Green, "Присутствия: $presenceCount")
                }
                if (absenceCount > 0) {
                    LegendItem(Color.Red, "Отсутствия: $absenceCount")
                }
            }
        }
    }

    @Composable
    fun LegendItem(color: Color, text: String) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(text)
        }
    }
    //endregion

    @Composable
    private fun ChooseVisitsDataPage() {
        var subjectList by remember {mutableStateOf<List<String>>(emptyList())}
        var month by remember {mutableStateOf<String>("")}
        var classList by remember {mutableStateOf<List<String>>(emptyList())}

        var isSubjectChoosed by remember {mutableStateOf(false)}
        var isClassChoosed by remember {mutableStateOf(false)}
        var isMonthChoosed by remember {mutableStateOf(false)}
        var isDayChoosed by remember {mutableStateOf(false)}

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            LaunchedEffect(Unit) {
                subjectList = getAllSubjects()
                classList = getAllClasses("Visits")
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // Список предметов
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    items(subjectList) { subject ->
                        Text(
                            text = subject,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    isSubjectChoosed = true
                                    chosenSubject.value = subject
                                }
                                .padding(10.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Сетка месяцев
                if (isSubjectChoosed == true) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(months.size) { i ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                                    .clickable {
                                        month = months[i]
                                        chosenMonth.value = month
                                        isMonthChoosed = true
                                    }
                                    .padding(6.dp)
                            ) {
                                Text(
                                    months[i],
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                // Сетка дней
                if (isMonthChoosed == true) {
                    val days = monthsWithDays[month]!!
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(6),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(days) { day ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFD0D0D0), RoundedCornerShape(6.dp))
                                    .clickable {
                                        chosenDay.value = day.toString()
                                        isDayChoosed = true
                                    }
                                    .padding(6.dp)
                            ) {
                                Text(
                                    day.toString(),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }

                // Список классов
                if (isDayChoosed == true) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8F8F8), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(classList.size) { i ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFE0E0E0), RoundedCornerShape(6.dp))
                                    .clickable {
                                        chosenClass.value = classList[i]
                                        isClassChoosed = true
                                    }
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = classList[i],
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    }
                }

                // Кнопка
                if (isClassChoosed) {
                    Button(
                        onClick = { navController.navigate("visitsPage") },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 12.dp)
                    ) {
                        Text("Выбрать", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
