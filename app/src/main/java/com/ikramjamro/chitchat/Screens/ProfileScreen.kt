package com.ikramjamro.chitchat.Screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ikramjamro.chitchat.CCViewModel
import com.ikramjamro.chitchat.DestinationScreens
import com.ikramjamro.chitchat.commonDivider
import com.ikramjamro.chitchat.commonImage
import com.ikramjamro.chitchat.commonProgressBar
import com.ikramjamro.chitchat.navigateTO

@Composable
fun ProfileScreen(navController: NavController, vm: CCViewModel) {
    val inProcess = vm.inProgress.value
    if(inProcess){
        commonProgressBar()
    }else {

        val userData = vm.userData.value
        var name by rememberSaveable {
            mutableStateOf(userData?.name?: "")
        }
        var number by rememberSaveable {
            mutableStateOf(userData?.number?: "")
        }
        ProfileContent(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(8.dp),
            vm=vm,
            name = name,
            number = number,
            onNameChange = {name=it},
            onNumberChange = {number=it},
            onSave = {
                     vm.CreateOrUpdateUserProfile(name=name,number=number)
            },
            onBack = {
                     navigateTO(navCollector = navController , route = DestinationScreens.ChatList.route)
            },
            onLogOut = {
                vm.logout()
                navigateTO(navCollector = navController, route = DestinationScreens.Login.route)
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 5.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomNavMenu(
                selectedItem = BottomNavigationItems.PROFILE,
                navController = navController
            )
        }
    }
}

@Composable
    fun ProfileContent(
                       modifier: Modifier,
                       vm: CCViewModel,
                       name:String,
                       number: String,
                       onNameChange: (String)->Unit,
                       onNumberChange: (String)->Unit,
                       onBack: ()-> Unit ,
                       onSave: ()-> Unit,
                       onLogOut: ()-> Unit
    ) {

        Column(modifier = Modifier.fillMaxSize()) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(10.dp),
                     horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(text = "Back", fontSize = 20.sp, modifier = Modifier
                    .clickable {
                         onBack.invoke()
                    })

                Text(text = "Save", fontSize = 20.sp, modifier = Modifier
                    .clickable {
                        onSave.invoke()
                    }
                )
            }

            commonDivider()
            ProfileImage(vm=vm)

            commonDivider()

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Name", modifier = Modifier
                    .width(80.dp)
                    .padding(horizontal = 10.dp))
                TextField(value = name, onValueChange = onNameChange,
                   colors = TextFieldDefaults.colors(
                       focusedTextColor = Color.Black,
                       focusedContainerColor = Color.Transparent,
                       unfocusedContainerColor = Color.Transparent,
                       disabledContainerColor = Color.Transparent
                   )
                    )


            }

            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(text = "Number", modifier = Modifier
                    .width(80.dp)
                    .padding(horizontal = 10.dp))
                TextField(value = number, onValueChange = onNumberChange,
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent
                    )
                )


            }

            commonDivider()
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
                   horizontalArrangement = Arrangement.Center
                ) {
                   Text(text = "Log Out", modifier = Modifier.clickable { onLogOut.invoke()})
            }


        }

    }

@Composable
fun ProfileImage(vm : CCViewModel){

    // this allows to select image from the device
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
        uri ->
        uri?.let {
            vm.uploadProfileImage(uri)
        }
    }

    val imageUri = vm.userData.value?.imageUrl

    Box(modifier=Modifier.height(intrinsicSize = IntrinsicSize.Min)) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                launcher.launch("image/*")
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)) {

                    commonImage(data = imageUri)
                
            }

            Text(text= "Change Profile Image")


        }

        if(vm.inProgress.value){
            commonProgressBar()
        }

    }


}