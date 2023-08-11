package com.example.appchat

import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.graphics.Color.GRAY
import android.graphics.Color.RED
import android.graphics.Color.parseColor
import android.net.ConnectivityManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import com.example.appchat.Activity.IpEmailForgot
import com.example.appchat.Activity.Main
import com.example.appchat.Activity.RegisterActivity
import com.example.appchat.Object.Account
import com.example.appchat.ui.theme.AppChatTheme
import com.example.appchat.ui.theme.blue
import com.example.appchat.ui.theme.white
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.zyao89.view.zloading.ZLoadingDialog
import com.zyao89.view.zloading.Z_TYPE

const  val  nameShare = "HOANG_DEVICE"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNetwork.context = this
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        val check = checkNetwork()
        registerReceiver(check, intentFilter)
        if(checkLogin())
            startActivity(Intent(this,Main::class.java))
        setContent {
            AppChatTheme {
                LoginScreen()
            }
        }
    }


    var LAccount = FirebaseDatabase.getInstance().getReference("LAccount")

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun LoginScreen() {
        var context = LocalContext.current
        var intent = Intent(context, RegisterActivity::class.java)

        Box(
            Modifier
                .fillMaxSize()
                .background(color = white)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Image(painterResource(id = R.drawable.top_tight), contentDescription = "")
                Text(
                    text = "Đăng nhập",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = blue, modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp)
                )
                CreateInput()
                TextButton(onClick =
                {
                    startActivity(context, intent, null)
                }, Modifier.fillMaxWidth())
                {
                    Text(text = "Chưa có tài khoản? Đăng ký", fontSize = 16.sp, color = blue)
                }
            }
            Image(
                painterResource(id = R.drawable.bottom_left),
                contentDescription = "",
                modifier = Modifier.align(Alignment.BottomStart)
            )
        }
    }

    @SuppressLint("SuspiciousIndentation")
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CreateInput() {
        var txtEmail by remember {
            mutableStateOf("")
        }
        var txtPass by remember {
            mutableStateOf("")
        }
        var isErrorMail by remember {
            mutableStateOf(false)
        }
        var isErrorPass by remember {
            mutableStateOf(false)
        }
        var isHidePass by remember {
            mutableStateOf(false)
        }
        var modifierText = Modifier
            .padding(bottom = 5.dp, start = 20.dp, end = 20.dp)
            .fillMaxWidth()
        OutlinedTextField(
            value = txtEmail,
            onValueChange = {
                txtEmail = it
                isErrorMail = if (txtEmail == "") true else false
            }
            , singleLine = true,

            textStyle = TextStyle(fontSize = 18.sp),
            modifier = modifierText.padding(top = 15.dp),
            isError = isErrorMail,
            label = { Text(text = "Email") },
            leadingIcon = { Icon(imageVector = Icons.Default.Email, contentDescription = "") },
            trailingIcon = {
                IconButton(onClick = { txtEmail = "" }) {
                    if (txtEmail != "") Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = ""
                    )

                }
            }
        )
        if (isErrorMail)
            Text(
                text = "Chưa nhập emal", modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth(), color = Color.Red
            )

        OutlinedTextField(
            value = txtPass,
            onValueChange = {
                txtPass = it
                isErrorPass = if (txtPass == "") true else false
            },
            textStyle = TextStyle(fontSize = 18.sp),
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            isError = isErrorPass,
            singleLine = true,
            label = { Text(text = "Password") },
            leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "") },
            trailingIcon = {
                IconButton(onClick = { isHidePass = !isHidePass }) {
                    if (txtPass != "") Icon(
                        imageVector = if (isHidePass) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = ""
                    )

                }
            },
            visualTransformation = if (isHidePass) PasswordVisualTransformation('.') else VisualTransformation.None
        )
        if (isErrorPass)
            Text(
                text = "Chưa nhập password", modifier = Modifier
                    .padding(start = 20.dp)
                    .fillMaxWidth(), color = Color.Red
            )

        var context = LocalContext.current
        var it = Intent(context, IpEmailForgot::class.java)
        Text(text = "Quên mật khẩu ?", fontSize = 16.sp, modifier = Modifier
            .padding(end = 20.dp, top = 5.dp)
            .clickable {
                startActivity(context, it, null)
            })

        Button(onClick = {
            isErrorMail = if (txtEmail == "") true else false
            isErrorPass = if (txtPass == "") true else false
            if (!txtEmail.isEmpty() && !txtPass.isEmpty()) {
                val dialog = ZLoadingDialog(context)
                dialog.setLoadingBuilder(Z_TYPE.SNAKE_CIRCLE) //设置类型
                    .setLoadingColor(RED) //颜色
                    .setHintText("Loading...")
                    .setCancelable(false)
                    .setHintTextSize(16f) // 设置字体大小 dp
                    .setHintTextColor(GRAY) // 设置字体颜色
                    .setDurationTime(0.5) // 设置动画时间百分比 - 0.5倍
                    .setDialogBackgroundColor(parseColor("#CC111111")) // 设置背景色，默认白色
                    .show()
                LAccount.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (sn in snapshot.children) {
                            val ac = sn.getValue(Account::class.java)
                            if (ac!!.email == txtEmail && ac.password == txtPass)
                            {
                                dialog.dismiss()
                                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                                saveAcc(ac.id)
                                variable.account = Account()
                                variable.account.id = ac.id
                                val it = Intent(context, Main::class.java)
                                startActivity(it)
                                return
                            }
                        }
                        dialog.dismiss()
                        Toast.makeText(context,"Tài khoản, mật khẩu chưa chính xác",Toast.LENGTH_SHORT).show();
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        },
            shape = RoundedCornerShape(5.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 20.dp, end = 20.dp),
            colors = ButtonDefaults.buttonColors(containerColor = blue)

        )
        {
            Text(
                text = "ĐĂNG NHẬP", fontSize = 18.sp, modifier = Modifier.padding(vertical = 6.dp)
            )
        }
    }


    fun saveAcc(id : Long)
    { // lưu tài khoản vào bộ nhớ người dùng
        val sharedPreferences: SharedPreferences = getSharedPreferences(nameShare, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong("idAccount", id)
        editor.apply()
    }
   fun  checkLogin() : Boolean
   {
       val sharedPreferences: SharedPreferences = getSharedPreferences(nameShare, MODE_PRIVATE)
       var id:Long = sharedPreferences.getLong("idAccount",0)
       if(id != 0L)
       {
           variable.account = Account(id,null,null) // lấy id acc để lấy dữ liệu của acc đó về
           return true
       }
       return false
   }
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun GreetingPreview() {
        AppChatTheme {
            LoginScreen()
        }
    }

    override fun onBackPressed() {
        finishAffinity()
        super.onBackPressed()

    }
}