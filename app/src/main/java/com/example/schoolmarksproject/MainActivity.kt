package com.example.schoolmarksproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
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
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.schoolmarksproject.ui.theme.SchoolMarksProjectTheme
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import com.example.schoolmarksproject.Models.Mark
import com.example.schoolmarksproject.Models.User
import com.google.common.base.Converter
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.getValue
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.HashMap
import java.util.Locale
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
    private var subjects = listOf("Основы геодезии",
            "Топографическая съемка",
            "Геодезические инструменты и оборудование",
            "Геоинформационные системы",
            "Измерения и расчеты в геодезии",
            "Проектирование геодезических работ",
            "Картография и картографические технологии",
            "Геодезические сети и их обработка",
            "Землеустройство и кадастр",
            "Геодезия и строительная геодезия")
    var directory = mapOf(
        "Можно ли редактировать оценки?" to "Да, выберите нужную оценку и нажмите \"Редактировать\", чтобы внести изменения.",
        "Не могу войти в систему, что делать?" to "Если вы не можете войти в систему, попробуйте следующие шаги:\n" +
                "1. Проверьте правильность введённых логина и пароля.\n" +
                "2. Убедитесь, что клавиша Caps Lock выключена.\n" +
                "3. Используйте функцию \"Забыли пароль?\", чтобы сбросить пароль.",
        "Как исправить оценку?" to "Чтобы исправить оценку, выполните следующие шаги:\n" +
                "1. Откройте раздел \"Оценки\" для данного студента.\n" +
                "2. Найдите оценку, которую необходимо изменить, и нажмите \"Редактировать\".\n" +
                "3. Внесите необходимые изменения в оценку и сохраните изменения.",
        "Что делать, если я не согласен с оценкой" to " Свяжитесь с преподавателем и обсудите возможность пересмотра оценки.",
        "Как узнать расписание экзаменов?" to "Расписание экзаменов можно посмотреть, на сайте колледжа.",
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
    private var MarksListOfTheClass = mutableStateListOf<Mark>()
    private var MarksListOfTheUser = mutableStateListOf<Mark>()
    private var isDataLoaded = mutableStateOf(false)
    private var IsneedClassChoose = mutableStateOf(false)
    //endregion

    //region standard functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //logOut()
        var user = Firebase.auth.currentUser

        lifecycleScope.launch {
            try {
                checkAllSubjects()
                usersList.addAll(getAllUsers("Marks"))
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
                getAllClasses("Marks")
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
                        //SimpleAttendancePage()
                        //SubjectGradesPage()
                        //saveMarks()
                        loginPageMarks()
                    }

                    composable("monthsGrid") {
                        if (currentUser.role == "Учитель"){
                            Column {
                                header()
                                classListPage()
                            }

                        }else{
                            if (IsneedClassChoose.value){
                                ChooseClassPage("Marks")
                            }else{
                                Column {
                                    header()
                                    SubjectGradesPage()
                                }
                            }

                            //MonthPage()

                        }
                    }
                    composable("daysGrid/{month}") { backStackEntry ->

                        val month = backStackEntry.arguments?.getString("month")
                        Column{
                            header()
                            DaysGrid(month!!)
                        }

                    }
                    composable("register") {
                        registerPageMarks()
                    }
                    composable("ClassChoosePage") {
                        Column {
                            header()
                            ChooseClassPage("Marks")
                        }
                    }
                    composable ("subjectChoosePage"){
                        Column {
                            header()
                            SubjectListPage()
                        }

                    }
                    composable ("classListPage" ){
                        Column(modifier = Modifier.fillMaxSize()) {
                            header()
                            classListPage()
                        }
                    }
                    composable("monthsPage") {
                        Column {  header()
                            MonthPage()
                        }

                    }
                    composable("classDayMarks") {
                        Column {
                            header()
                            ClassDayMarks()
                        }
                    }
                    composable ("subjectGradesPage"){
                        Column {  header()
                            SubjectGradesPage()
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
                    composable ("reportPage"){
                        Column {
                            header()
                            ReportPage()
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


    private fun addUserWithRole(itemSaveCommand : (String, String, String, String) -> Unit) {
        itemSaveCommand(email.value, role.value, name.value, surname.value);
    }
    //region Marks logic

    //endregion

    //region visitsLogic
    //endregion

    // region workWithDatabase

    //classesDivision - тип приложения, с оценками или посещениями
    private fun setClass(classNumber : String){
        var database = Firebase.database.reference
        var dataRef = database.child("Marks").child("Users").child(email.value.split('.')[0])
        currentUser.classNumber = classNumber
        chosenClass.value = classNumber
        dataRef.child(classNumber).setValue(classNumber)

    }

    private suspend fun checkAllSubjects(){
        val database = Firebase.database.reference
        val dataRef = database.child("Marks").child("Marks")

        val snapshot = dataRef.get().await()
        val existingSubjects = mutableSetOf<String>()

        // Собираем уже существующие предметы
        snapshot.children.forEach { classSnapshot ->
            classSnapshot.children.forEach { subjectSnapshot ->
                existingSubjects.add(subjectSnapshot.key.toString())
            }
        }

        // Вычисляем, каких предметов не хватает
        val missingSubjects = subjects.filterNot { existingSubjects.contains(it) }

        if (missingSubjects.isNotEmpty()) {
            for (classSnapshot in snapshot.children) {
                val classKey = classSnapshot.key ?: continue
                val classRef = dataRef.child(classKey)

                for (subject in missingSubjects) {
                    // Создаем пустой узел для предмета, чтобы от него потом можно было строить даты/оценки
                    classRef.child(subject).setValue("") // можно заменить на null или mapOf(...) при необходимости
                }
            }
        }
    }

    private suspend fun getAllClasses(classesDivision : String){

        var database = Firebase.database.reference

        var dataRef = database.child(classesDivision).child("Classes")

        var snapshot = dataRef.get().await()

        var x = snapshot.children.map { it ->
           it.value.toString()
        }
        classList.addAll(x)
    }

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

    private suspend fun getAllSubjects() : List<String>{
        var database = Firebase.database.reference
        var dataRef = database.child("Marks").child("Subjects")
        var snapshot = dataRef.get().await()
        var subjects = snapshot.children.map { it.value.toString() }

        return subjects

    }

    private suspend fun createClassAndAdd(className : String){
        var subjects = getAllSubjects()
        var database = Firebase.database.reference.child("Marks")

        database.child("Classes").child(className).setValue(className)
        subjects.forEach {subject ->
            database.child("Marks").child(className).child(subject).setValue("")
        }
    }

    private fun getAllUsersStudents(classesDivision: String, onComplete: () -> Unit = {}) {
        usersList.clear()
        val database = Firebase.database.reference
        val dataRef = database.child(classesDivision).child("Users")

        dataRef.get().addOnSuccessListener { snapshot ->
            snapshot.children.forEach { user ->
                val name = user.child("name").getValue(String::class.java) ?: ""
                val surname = user.child("surname").getValue(String::class.java) ?: ""
                val email = user.child("email").getValue(String::class.java) ?: ""
                val role = user.child("role").getValue(String::class.java) ?: ""

                if (role == "Ученик") {
                    val classNumber = user.child("classNumber").getValue(String::class.java) ?: ""
                    usersList.add(User(name, surname, role, email, classNumber))
                }
            }
            onComplete() // Уведомляем об окончании загрузки
        }
    }

    private fun saveUserToFirebaseMarks(email : String, role : String, name : String, surname : String) : Unit{
        var database = Firebase.database.reference
        var dataRef = database.child("Marks").child("Users").child(email.split('.')[0])

        dataRef.setValue(User(name, surname, role, email))
    }

    private fun setMark(classNumber: String, studentEmail: String, markValue: Int, subject: String, date: String, month: String = "Январь") {
        if (currentUser.role != "Учитель") {
            Log.e("Firebase", "Только учителя могут выставлять оценки.")
            return
        }

        val studentName = studentEmail.substringBefore(".") // имя из email (например, "igor2")
        val mark = Mark(name = studentName, mark = markValue, email = studentEmail)

        val database = FirebaseDatabase.getInstance().getReference("Marks")
        val markRef = database.child("Marks").child(classNumber).child(subject).child(month).child(date).child(studentName)

        markRef.setValue(mark)
            .addOnSuccessListener {
                Log.d("Firebase", "Оценка успешно добавлена.")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Ошибка при добавлении оценки: ${e.message}")
            }
    }

    fun parseRussianDate(month: String, day: String, year: Int = 2025): Date? {
        val monthNumber = when (month.lowercase()) {
            "январь" -> 1
            "февраль" -> 2
            "март" -> 3
            "апрель" -> 4
            "май" -> 5
            "июнь" -> 6
            "июль" -> 7
            "август" -> 8
            "сентябрь" -> 9
            "октябрь" -> 10
            "ноябрь" -> 11
            "декабрь" -> 12
            else -> return null
        }

        val dateString = "%02d-%02d-%04d".format(day.toInt(), monthNumber, year)
        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale("ru"))
        println(formatter.parse(dateString).toString())
        return formatter.parse(dateString)
    }

    private suspend fun getStudentMarksBySubject(classNumber: String, subject: String, month: String = "сентябрь", studentEmail : String ) : HashMap<Date, Mark?> {
        val semesterStartDate = parseRussianDate(semestrStartMonth, semestrStartDay.toString(), 2025)
        val semesterEndDate = parseRussianDate(semestrEndMonth, semestrEndDay.toString(), 2025)
        var dataSubjectMarkDictionary : HashMap<Date, Mark?> = HashMap<Date, Mark?>()
        var dates = generateAllDatesInSemester(semestrStartMonth, semestrStartDay, semestrEndMonth, semestrEndDay, subject = "Math")
        dates.forEach { dataSubjectMarkDictionary.put(it, null) }
        val database = FirebaseDatabase.getInstance().getReference("Marks")
        val marksRef = database.child("Marks").child(classNumber).child(subject)
        var snapshot = marksRef.get().await()
        for (monthSnapshot in snapshot.children) {
            for (dateSnapshot in monthSnapshot.children){
                val markSnapshot = dateSnapshot.child(studentEmail)
                if (markSnapshot.exists()) {
                    val mark = markSnapshot.getValue(Mark::class.java)
                    mark?.let { MarksListOfTheUser.add(it) }
                    var markDate = parseRussianDate(monthSnapshot.key.toString(), dateSnapshot.key.toString())
                    var key = markDate!!
                    if (mark != null && markDate != null &&
                        !markDate.before(semesterStartDate) && !markDate.after(semesterEndDate)){
                        dataSubjectMarkDictionary.put(key, mark)
                    }
                }
            }
        }
        return dataSubjectMarkDictionary;
    }

    suspend fun getClassMarksByDay(
        classNumber: String,
        subject: String,
        day: Int,
        month: String = "Январь"
    ): List<Mark> {
        val database = Firebase.database.reference
        // Шаг 1: Получить всех учеников из класса
        val usersSnapshot = database.child("Marks").child("Users").get().await()
        val studentsInClass = usersSnapshot.children.mapNotNull { user ->
            val role = user.child("role").getValue<String>() ?: return@mapNotNull null
            val studentsClassNumber = user.child("classNumber").getValue<String>()
            if (role == "Ученик" && studentsClassNumber == classNumber) {
                val name = user.child("name").getValue<String>() ?: ""
                val surname = user.child("surname").getValue<String>() ?: ""
                val email = user.child("email").getValue<String>() ?: ""
                val key = user.key ?: "" // Например, "igor2mail"
                Triple(key, name, surname to email)
            } else null
        }.associateBy({ it.first }) { Triple(it.second, it.third.first, it.third.second) }

        val cleanClassNumber = classNumber.trim()
        val cleanSubject = subject.trim()
        val cleanMonth = month.trim()
        Log.d("FIREBASE_DEBUG", "path = Marks/$cleanClassNumber/$cleanSubject/$cleanMonth")
        // Шаг 2: Получить оценки по subject → месяц → день
        val marksSnapshot = database
            .child("Marks")
            .child("Marks")
            .child(classNumber)
            .child(subject)
            .child(month)
            .child(day.toString())
            .get()
            .await()
        Log.d("FIREBASE_DEBUG", "childrenCount = ${marksSnapshot.childrenCount}")


        val marksMap = marksSnapshot.children.associate { markNode ->
            val mark = markNode.child("mark").getValue<Int>()
            val key = markNode.key ?: ""
            key to mark
        }

        // Шаг 3: "Left join" учеников и оценок
        val result = studentsInClass.map { (userKey, studentData) ->
            val mark = marksMap[userKey] ?: 0 // может быть null
            Mark(
                name = studentData.first,
                email = studentData.third,
                mark = mark
            )
        }
        return result
    }

    private fun saveUsersClass(){
        var database = Firebase.database.reference
        var dataRef = database.child("Marks").child("Users").child(email.value.substringBefore('.'))

        dataRef.child("classNumber").setValue(classNumber.value)
    }

    private fun generateAllDatesInSemester(
        startMonth: String,
        startDay: Int,
        endMonth: String,
        endDay: Int,
        subject: String
    ): List<Date> {
        val allDates = mutableListOf<Date>()
        val startMonthIndex = months.indexOf(startMonth)
        val endMonthIndex = months.indexOf(endMonth)

        for (monthIndex in startMonthIndex..endMonthIndex) {
            val month = months[monthIndex]
            val daysInMonth = monthsWithDays[month]

            val startDayInMonth = if (monthIndex == startMonthIndex) startDay else 1
            val endDayInMonth = if (monthIndex == endMonthIndex) endDay else daysInMonth

            for (day in startDayInMonth..endDayInMonth!!) {
                var date = parseRussianDate(month, day.toString())
                allDates.add(date!!) // Пример ключа: "Math_Сентябрь_1"
            }
        }

        return allDates
    }

    //endregion
    private fun getUserRole(email: String) {
        var database = Firebase.database.reference
        var userRef = database.child("Users").child(email)

        userRef.child("role").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var role = snapshot.getValue(String::class.java)

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error loading user role", error.toException())
            }

        })
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

    private fun registerMarks() {
        if (email.value.isEmpty() || password.value.isEmpty() || name.value.isEmpty() || surname.value.isEmpty()) {
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
        auth.createUserWithEmailAndPassword(email.value, password.value.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addUserWithRole { email, role, name, surname ->
                        saveUserToFirebaseMarks(email, role, name, surname)
                    }
                    user = auth.currentUser!!
                    if (role.value == "Ученик"){
                        navController.navigate("ClassChoosePage")
                    }else{
                        navController.navigate("classListPage")
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

    private fun registerVisits(){
        TODO()
    }

    private fun logOut() {
        Firebase.auth.signOut()
    }
    //endregion


    //region work with Database
    fun saveMarkToDatabse(month: String, day: Int, mark: Int) {
        var database = Firebase.database.reference
        var ratingRef = database.child("Май").child("17")
        ratingRef.setValue(4)

    }

    //endregion

    //region Composable Elements Marks
    @Composable
    private fun loginPageMarks() {
        var isPasswordVisible by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.align(Alignment.Center)
            ) {
                TextField(email.value, onValueChange = { newEmail ->
                    email.value = newEmail

                }, placeholder = { Text("Почта") })
                Spacer(modifier = Modifier.height(30.dp))
                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = { Text("Пароль") },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        Icon(
                            imageVector = image,
                            contentDescription = if (isPasswordVisible) "Скрыть пароль" else "Показать пароль",
                            modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    }
                )
                Spacer(modifier = Modifier.height(30.dp))
                Button(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF0285FF),
                        containerColor = Color.Gray
                    ),
                    shape = RectangleShape,
                    onClick = { login() },
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Войти", color = Color.Blue)
                }
                Button(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF0285FF),
                        containerColor = Color.LightGray
                    ),
                    shape = RectangleShape,
                    onClick = { navController.navigate("register") },
                    modifier = Modifier.width(150.dp)
                ) {
                    Text("Регистрация")
                }
            }

        }
    }
    @Composable
    private fun registerPageMarks() {
        var isPasswordVisible by remember { mutableStateOf(false) }
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    placeholder = { Text("Почта") },
                    modifier = Modifier.width(250.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                TextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    placeholder = { Text("Пароль") },
                    modifier = Modifier.width(250.dp),
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (isPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        Icon(
                            imageVector = image,
                            contentDescription = if (isPasswordVisible) "Скрыть пароль" else "Показать пароль",
                            modifier = Modifier.clickable { isPasswordVisible = !isPasswordVisible }
                        )
                    }
                )

                Spacer(modifier = Modifier.height(50.dp))

                TextField(
                    value = name.value,
                    onValueChange = { name.value = it },
                    placeholder = { Text("Имя") },
                    modifier = Modifier.width(250.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                TextField(
                    value = surname.value,
                    onValueChange = { surname.value = it },
                    placeholder = { Text("Фамилия") },
                    modifier = Modifier.width(250.dp)
                )

                Spacer(modifier = Modifier.height(50.dp))

                createSelect("Роль", roles, role)

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF0285FF),
                        containerColor = Color.Gray
                    ),
                    onClick = {
                        registerMarks()
                    },
                    modifier = Modifier.width(250.dp)
                ) {
                    Text("Зарегистрироваться", color = Color.Blue)
                }

                Spacer(modifier = Modifier.height(50.dp))

                Button(
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color(0xFF0285FF),
                        containerColor = Color.LightGray
                    ),
                    onClick = { navController.navigate("login") },
                    modifier = Modifier.width(250.dp)
                ) {
                    Text("Войти")
                }
            }
        }
    }
    //endregion

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

                Spacer(modifier = Modifier.height(32.dp))

                // Кнопка регистрации
                FilledTonalButton(
                    onClick = { registerVisits() },
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

    @Composable fun ChooseClassPage(classesDivision: String){
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
            Column {
                createSelect("Ваш класс", classList, classNumber)
                Spacer(modifier = Modifier.height(50.dp))
                Button(onClick = {
                    if (classNumber.value == "" || classNumber.value == "Ваш класс"){
                        return@Button
                    }
                    setClass(classNumber.value)
                    saveUsersClass()
                    IsneedClassChoose.value = false
                    navController.navigate("monthsGrid")

                }) {
                    Text("Сохранить")
                }
            }

        }

    }

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
    fun MonthPage() {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // светлый фон, как и в классе
        ) {
            Column {
                Text(
                    text = "Months of the Year",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(16.dp),
                    color = Color(0xFF3F51B5), // акцентный синий цвет
                    fontWeight = FontWeight.Bold
                )
                MonthsGrid()
            }
        }
    }

    @Composable
    fun MonthsGrid() {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 3 элемента в строке
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(months.size) { month ->
                Card(
                    modifier = Modifier
                        .height(80.dp)
                        .fillMaxWidth()
                        .clickable {
                            chosenMonth.value = months[month]
                            navController.navigate("daysGrid/${months[month]}")
                        },
                    shape = RoundedCornerShape(10.dp),
                    colors = cardColors(containerColor = Color.White),
                    elevation = cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = months[month],
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }



    @Composable
    fun DaysGrid(month: String) {
        val days = monthsWithDays[month]

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5)) // общий фон
                .padding(16.dp)
        ) {
            Text(
                text = "Выберите день",
                style = MaterialTheme.typography.headlineSmall,
                color = Color(0xFF3F51B5),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items((days as Int) + 1) { day ->
                    if (day != 0) {
                        Card(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .clickable {
                                    chosenDay.value = day.toString()
                                    navController.navigate("classDayMarks")
                                },
                            shape = RoundedCornerShape(10.dp),
                            colors = cardColors(containerColor = Color.White),
                            elevation = cardElevation(defaultElevation = 4.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text(
                                    text = day.toString(),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun ClassDayMarks() {
        var marksList by remember { mutableStateOf<List<Mark>>(emptyList()) }

        LaunchedEffect(Unit) {
            marksList = getClassMarksByDay(
                chosenClass.value,
                chosenSubject.value,
                day = chosenDay.value.toInt(),
                month = chosenMonth.value
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp)
        ) {
            Text(
                text = "Оценки за ${chosenDay.value} ${chosenMonth.value.lowercase()}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3F51B5),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            "Ученик",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            "Оценка",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(80.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                itemsIndexed(marksList) { index, mark ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mark.name,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = if (mark.mark in 2..5) mark.mark.toString() else "",
                            onValueChange = { newValue ->
                                when {
                                    newValue.isEmpty() -> {
                                        marksList = marksList.toMutableList().apply {
                                            set(index, mark.copy(mark = 0))
                                        }
                                    }

                                    newValue.toIntOrNull() in 2..5 -> {
                                        marksList = marksList.toMutableList().apply {
                                            set(index, mark.copy(mark = newValue.toInt()))
                                        }
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier
                                .width(80.dp)
                                .height(48.dp),
                            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    marksList.forEach {
                        setMark(
                            classNumber = chosenClass.value,
                            studentEmail = it.email,
                            markValue = it.mark,
                            subject = chosenSubject.value,
                            date = chosenDay.value,
                            month = chosenMonth.value
                        )
                    }
                    navController.navigate("daysGrid/${chosenMonth.value}")
                }
            ,  modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)) {
                Text("Сохранить")
            }
        }
    }
//    @Composable
//    private fun UserMarks(){
//        var marksDictionary by remember { mutableStateOf<Map<Date, Mark?>>(emptyMap()) }
//
//        LaunchedEffect(Unit) {
//            marksDictionary = getStudentMarksBySubject(chosenClass.value, "Math", studentEmail = "igor2@mail")
//        }
//
//        Box (modifier = Modifier.fillMaxSize()){
//            if (!isDataLoaded.value) {
//                CircularProgressIndicator()
//            } else {
//                LazyColumn(horizontalAlignment = Alignment.CenterHorizontally,
//                    modifier = Modifier.align(Alignment.Center)) {
//                    items(marksDictionary.keys.toList()) { key ->
//                        Text(key.toString())
//                        Text("${marksDictionary[key]?.mark?: ""}")
//                    }
//                }
//            }
//
//        }
//    }

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

    //endregion

    @Composable
    fun SubjectGradesPage() {
        var subjects2 by remember { mutableStateOf<List<String>>(emptyList()) }
        val subjectWithMarks = remember { mutableStateMapOf<String, MutableMap<Date, Mark?>>() }
        LaunchedEffect(Unit) {
            subjects2 = getAllSubjects()
            subjects2.forEach {
                subjectWithMarks.put(
                    it,
                    getStudentMarksBySubject(
                        currentUser.classNumber!!,
                        it,
                        studentEmail = currentUser.email!!.substringBefore('.')!!
                    )
                )
            }
        }
        SubjectMarksTable(subjectWithMarks)
    }

    @Composable
    fun SubjectMarksTable(subjectWithMarks: Map<String, Map<Date, Mark?>>) {

        var marksNotification by remember {mutableStateOf<HashSet<String>>(HashSet<String>())}
        val allDates = subjectWithMarks.values
            .flatMap { it.keys }
            .distinct()
            .sorted()

        val dateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())

        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5))
                        .padding(8.dp)
                ) {
                    // 1. Fixed left column with subject names
                    Column(
                        modifier = Modifier
                            .width(100.dp)
                            .verticalScroll(verticalScrollState)
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            )
                            .border(1.dp, Color.LightGray)
                    ) {
                        Spacer(modifier = Modifier.height(40.dp)) // header space
                        subjectWithMarks.keys.forEach { subject ->
                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(4.dp)
                                    .fillMaxWidth()
                                    .background(Color(0xFFE8EAF6), shape = RoundedCornerShape(6.dp))
                                    .border(
                                        1.dp,
                                        Color(0xFFB0BEC5),
                                        shape = RoundedCornerShape(6.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = subject,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    modifier = Modifier.clickable {
                                        chosenSubject.value = subject
                                        navController.navigate("reportPage")
                                        //ReportPage()
                                    })
                            }
                        }
                    }

                    // 2. Scrollable center with date marks
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .horizontalScroll(horizontalScrollState)
                            .verticalScroll(verticalScrollState)
                            .background(Color.White)
                            .border(1.dp, Color.LightGray)
                    ) {
                        // Header with dates
                        Row {
                            allDates.forEach { date ->
                                Box(
                                    modifier = Modifier
                                        .width(40.dp)
                                        .height(40.dp)
                                        .background(
                                            Color(0xFFCFD8DC),
                                            shape = RoundedCornerShape(6.dp)
                                        )
                                        .border(1.dp, Color.Gray, shape = RoundedCornerShape(6.dp))
                                        .padding(2.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dateFormat.format(date),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Table body with marks
                        subjectWithMarks.forEach { (_, marksByDate) ->
                            Row {
                                allDates.forEach { date ->
                                    val mark = marksByDate[date]?.mark ?: "—"
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(40.dp)
                                            .padding(2.dp)
                                            .background(
                                                Color(0xFFE3F2FD),
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .border(
                                                1.dp,
                                                Color(0xFF90CAF9),
                                                shape = RoundedCornerShape(4.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = mark.toString())
                                    }
                                }
                            }
                        }
                    }

                    // 3. Fixed right column with averages
                    Column(
                        modifier = Modifier
                            .width(60.dp)
                            .verticalScroll(verticalScrollState)
                            .background(
                                Color.White,
                                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            )
                            .border(1.dp, Color.LightGray)
                    ) {
                        // Header
                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .background(Color.DarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Средняя",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Averages
                        subjectWithMarks.forEach { (subject, marksByDate) ->
                            val numericMarks =
                                marksByDate.values.filter { it != null }.map { it!!.mark }

                            val avg = if (numericMarks.isNotEmpty()) {
                                numericMarks.average()

                            } else {
                                null
                            }
                            if (avg != null && avg < 3.5 ) {
                                marksNotification.add(subject)
                            }

                            Box(
                                modifier = Modifier
                                    .height(40.dp)
                                    .padding(2.dp)
                                    .background(Color(0xFFD1C4E9), shape = RoundedCornerShape(4.dp))
                                    .border(
                                        1.dp,
                                        Color(0xFF9575CD),
                                        shape = RoundedCornerShape(4.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = avg?.let { String.format("%.1f", it) } ?: "—",
                                    color = Color(0xFF3F51B5),
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(40.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                if (marksNotification.size > 0){
                    Box(
                    ){
                        Badge {
                            Text(marksNotification.size.toString())
                        }
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = Color.Red,
                            modifier = Modifier
                                .size(40.dp)
                                .padding(start = 2.dp).clickable{
                                    var sb = StringBuilder()
                                    var subjects = marksNotification.forEach {
                                        sb.append("${it};")
                                    }
                                    navController.navigate("notificationsDescription/${sb.toString()}")
                                }
                        )
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
                            text = "Мои оценки",
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.clickable{navController.navigate("subjectGradesPage")}
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
                            text = "Электронный дневник",
                            Modifier.clickable{navController.navigate("classListPage")},
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
    private fun ReportPage() {
        var subjects2 by remember { mutableStateOf<List<String>>(emptyList()) }
        var subjectMarks by remember { mutableStateOf<List<Mark>>(emptyList()) }

        LaunchedEffect(Unit) {
            subjects2 = getAllSubjects()
            subjectMarks = getStudentMarksBySubject(
                currentUser.classNumber!!,
                chosenSubject.value,
                studentEmail = currentUser.email!!.substringBefore('.')!!
            ).filter { it.value != null }
                .mapNotNull { it.value }
        }

        val groupedMarks = subjectMarks.groupingBy { it.mark }.eachCount()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text("Статистика оценок по предмету: ${chosenSubject.value}", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))

            if (groupedMarks.isNotEmpty()) {
                BarChart(data = groupedMarks)
            } else {
               Text("Похоже по данному предмету у вас пока нет оценок")
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
    fun SimpleAttendancePage() {
        val date = "15 мая 2024"
        val subject = "Физика"
        val students = listOf(
            "Смирнов А." to "П",
            "Козлова А." to "П",
            "Новиков Д." to "Н",
            "Волкова Е." to "П",
            "Фёдоров М." to "Н",
            "Павлова С." to "П",
            "Лебедев И." to "П"
        )

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


            Spacer(Modifier.height(16.dp))

            // Таблица
            Row(Modifier.fillMaxWidth()) {
                // Колонка имен
                Column(Modifier.weight(1f)) {
                    Text("Ученик", fontWeight = FontWeight.Bold)
                    students.forEach { (name, _) ->
                        Text(name, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }

                // Колонка статусов
                Column(Modifier.width(60.dp)) {
                    Text("Статус", fontWeight = FontWeight.Bold)
                    students.forEach { (_, status) ->
                        Text(
                            text = status,
                            color = if (status == "П") Color.Green else Color.Red,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
    //endregion
}






@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SchoolMarksProjectTheme {
    }
}