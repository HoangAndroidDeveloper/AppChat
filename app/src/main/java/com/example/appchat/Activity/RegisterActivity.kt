
package com.example.appchat.Activity

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.appchat.MyFunction
import com.example.appchat.Object.Account
import com.example.appchat.Object.User
import com.example.appchat.R
import com.example.appchat.checkNetwork
import com.example.appchat.ui.theme.AppChatTheme
import com.example.appchat.ui.theme.blue
import com.example.appchat.variable
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class RegisterActivity : AppCompatActivity() {
     lateinit var context : Context
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNetwork.context = this
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val check = checkNetwork()
        registerReceiver(check, intentFilter)
        context = this
        setContent {
            AppChatTheme {
                RegisterScreen()
            }
        }
    }

    var putUser = FirebaseDatabase.getInstance().getReference("LUser")
    var LAccount = FirebaseDatabase.getInstance().getReference("LAccount")
    @Composable
    fun RegisterScreen() {
        Box(Modifier.fillMaxSize()) {
            Column() {
                Image(painterResource(id = R.drawable.top_left), contentDescription = "")
                Text(text = "Đăng ký", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = blue
                , modifier = Modifier.padding(start = 20.dp, top = 5.dp))
                CreateInput()
            }
            Image(painterResource(id = R.drawable.bottom_right), contentDescription = ""
            , Modifier.align(Alignment.BottomEnd))


        }

    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CreateInput() {
        var txtEmail by remember {
            mutableStateOf( "")
        }
        var txtName by remember {
            mutableStateOf( "")
        }
        var txtPass by remember {
            mutableStateOf( "")
        }
        var isErrorMail by remember {
            mutableStateOf("")
        }
        var isErrorPass by remember {
            mutableStateOf(false)
        }
        var isErrorName by remember {
            mutableStateOf(false)
        }
        var modifierText = Modifier
            .padding(bottom = 5.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()

        OutlinedTextField(value = txtName, onValueChange = {
            txtName = it
        }

            ,modifier = modifierText.padding(top = 15.dp)
            , isError = isErrorName
            , label = { Text(text = "Họ tên")}
            , singleLine = true
            , leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "" )}
            , trailingIcon = { IconButton(onClick = { txtName = "" }) {
                if(txtName != "") Icon(imageVector = Icons.Default.Clear, contentDescription = "")
            }}
        )
        if(isErrorName)
            Text(text = "Chưa nhập tên", modifier = Modifier.padding(start = 20.dp), color = Color.Red)

        OutlinedTextField(value = txtEmail, onValueChange = {
            txtEmail = it
        }
            ,modifier = modifierText
            , singleLine = true
            , isError = isErrorMail != ""
            , label = { Text(text = "Email")}
            , leadingIcon = { Icon(imageVector = Icons.Default.Email,contentDescription = "" )}
            , trailingIcon = { IconButton(onClick = { txtEmail = "" }) {
                if(txtEmail != "") Icon(imageVector = Icons.Default.Clear, contentDescription = "")
            }}
        )
        if(isErrorMail != "" )
            Text(text = isErrorMail, modifier = Modifier.padding(start = 20.dp), color = Color.Red)
        OutlinedTextField(value = txtPass, onValueChange = {
            txtPass = it
        }
            , singleLine = true
            ,modifier = modifierText
            , isError = isErrorPass
            , label = { Text(text = "Password")}
            , leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "" )}
            , trailingIcon = { IconButton(onClick = { txtPass = "" }) {
                if(txtPass != "") Icon(imageVector = Icons.Default.Clear, contentDescription = "")
            }}
        )

        if(isErrorPass)
            Text(text = "Chưa nhập password", modifier = Modifier.padding(start = 20.dp), color = Color.Red)
        Button(onClick = {
            isErrorMail = if(txtEmail == "") "Chưa nhập email" else ""
            isErrorPass = txtPass == ""
            isErrorName = txtName == ""

            if(txtEmail.isNotEmpty() && txtName.isNotEmpty() && txtPass.isNotEmpty())
            {
                LAccount.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (sn in snapshot.children) {
                            val ac = sn.getValue(Account::class.java)
                            if (txtEmail == ac!!.email)
                            {
                                Toast.makeText(this@RegisterActivity, "Email đã được sử dụng", Toast.LENGTH_SHORT).show()
                                break
                            }
                        }
                                val it = Intent(context,IpCode::class.java)
                                val id = MyFunction.CreateId()
                                variable.account = Account(id,txtEmail,txtPass)
                                val user = User()
                                user.name = txtName
                                user.id = id
                                variable.user = user
                                startActivity(it)
                            }



                    override fun onCancelled(error: DatabaseError) {}
                })

            }

        }
            , colors = ButtonDefaults.buttonColors(containerColor = blue)
            , shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)

        )
        {
            Text(text = "ĐĂNG KÝ", fontSize = 18.sp
                , modifier = Modifier.padding(vertical = 6.dp))
        }

    }


    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun Preview() {
        RegisterScreen()
    }
}
