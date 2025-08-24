package com.ikramjamro.chitchat.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ikramjamro.chitchat.CCViewModel
import com.ikramjamro.chitchat.CheckSignedIn
import com.ikramjamro.chitchat.DestinationScreens
import com.ikramjamro.chitchat.R
import com.ikramjamro.chitchat.commonProgressBar
import com.ikramjamro.chitchat.navigateTO

@Composable
fun loginScreen(vm: CCViewModel , navController: NavController){

    CheckSignedIn(vm=vm, navControl = navController)

        Box(modifier = Modifier.fillMaxSize()) {

            Column(modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                var emailState= remember {
                    mutableStateOf(TextFieldValue())
                }
                var passwordState= remember {
                    mutableStateOf(TextFieldValue())
                }
                var focus= LocalFocusManager.current

                Image(painter = painterResource(id = R.drawable.chat),
                    contentDescription = null,
                    modifier = Modifier
                        .width(200.dp)
                        .padding(top = 16.dp)
                        .padding(8.dp)
                )

                Text(text = "Sign In",
                    fontSize = 30.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(8.dp)
                )

                OutlinedTextField(value = emailState.value, onValueChange ={
                    emailState.value=it
                },
                    label = { Text(text = "Email")},
                    modifier = Modifier.padding(8.dp)
                )

                OutlinedTextField(value = passwordState.value, onValueChange ={
                    passwordState.value=it
                },
                    label = { Text(text = "Password")},
                    modifier = Modifier.padding(8.dp)
                )
                val context= LocalContext.current
                Button(onClick = {
                                 vm.logIn(context=context, email = emailState.value.text, password = passwordState.value.text)
                } , modifier = Modifier.padding(8.dp)) {
                    Text(text = "SIGN IN")
                }

                Text(
                    text = "New User ? Go to Sign Up -> ",
                    color = Color.Blue,
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            navigateTO(navController, DestinationScreens.SignUp.route)
                        }
                )



            }

        }

        if(vm.inProgress.value){
            commonProgressBar()
        }



    }