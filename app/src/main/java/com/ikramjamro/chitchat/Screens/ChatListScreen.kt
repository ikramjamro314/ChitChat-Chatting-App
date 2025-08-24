package com.ikramjamro.chitchat.Screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ikramjamro.chitchat.CCViewModel
import com.ikramjamro.chitchat.CommonRow
import com.ikramjamro.chitchat.CommonTextChat
import com.ikramjamro.chitchat.DestinationScreens
import com.ikramjamro.chitchat.commonProgressBar
import com.ikramjamro.chitchat.navigateTO

@Composable
fun ChatListScreen(navController: NavController, vm: CCViewModel) {
    var inProgress = vm.inProgressChats.value
    if (inProgress) {
        commonProgressBar()
    } else {

        val chats = vm.chats.value
        val userData=vm.userData.value
        val showDialog= remember {
            mutableStateOf(false)
        }
        var context= LocalContext.current

        val onFabClick:()->Unit={showDialog.value=true}
        val onDismiss:()->Unit={showDialog.value=false}
        val onAddChat:(String)->Unit={
            vm.addChat(context=context,it)
            showDialog.value=false
        }

        Scaffold(
            floatingActionButton= {
                fab(
                    showDialog = showDialog.value,
                    onDismiss = { onDismiss() },
                    onFabClick = { onFabClick() },
                    onAddChat = onAddChat
                )
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                    CommonTextChat("Chats")

                    if (chats.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(text = "No Chats Available", fontSize = 20.sp)
                        }
                    }else{
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(chats){
                                chat->
                                val chatUser = if(chat.user1.userID == userData?.userID){
                                    chat.user2
                                }else{
                                    chat.user1
                                }

                                    CommonRow(
                                        imageUrl = chatUser.imageUrl.toString(),
                                        name = chatUser.name.toString(),
                                    ) {
                                        chat.chatId.let{
                                        navigateTO(
                                            navController,
                                            DestinationScreens.SingleChat.createRoute(it.toString())
                                        )

                                    }
                                }

                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 5.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    BottomNavMenu(
                        selectedItem = BottomNavigationItems.CHATLIST,
                        navController = navController
                    )

                }
            }
    )

  }
}

@Composable
fun fab(
    showDialog: Boolean,
    onDismiss: ()-> Unit,
    onFabClick:()-> Unit,
    onAddChat:(String)->Unit
){
    val addChatNumber= remember {
        mutableStateOf("")
    }

    if(showDialog){
        AlertDialog(onDismissRequest = {
            onDismiss.invoke()
            addChatNumber.value=""
        },
            confirmButton = { Button ( onClick = { onAddChat(addChatNumber.value) } ){
                Text(text = "Add Chat")
            }
                            },
            title = {Text(text = "Add Chat")},
            text = {
                OutlinedTextField(value = addChatNumber.value,
                    onValueChange = {addChatNumber.value = it},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        
        )
    }
    FloatingActionButton(onClick = { onFabClick.invoke() },
                containerColor = MaterialTheme.colorScheme.secondary,
                shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
        ) {
        Icon(imageVector = Icons.Rounded.Add, contentDescription ="", tint = Color.White )

    }


}
