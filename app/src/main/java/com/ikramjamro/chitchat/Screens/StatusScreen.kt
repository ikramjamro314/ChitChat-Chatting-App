package com.ikramjamro.chitchat.Screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ikramjamro.chitchat.CCViewModel
import com.ikramjamro.chitchat.CommonRow
import com.ikramjamro.chitchat.CommonTextChat
import com.ikramjamro.chitchat.DestinationScreens
import com.ikramjamro.chitchat.commonDivider
import com.ikramjamro.chitchat.commonProgressBar
import com.ikramjamro.chitchat.navigateTO

@Composable
fun StatusScreen(navController: NavController, vm: CCViewModel) {


    val inProcess = vm.inProgressStatus.value
    if (inProcess) {
        commonProgressBar()
    } else {
        val statuses = vm.status.value
        val user = vm.userData.value

        val myStatuses = statuses.filter {
            it.user.userID == user?.userID
        }

        val otherStatuses = statuses.filter {
            it.user.userID != user?.userID
        }

        val context= LocalContext.current

        val launcher= rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            uri->
            uri?.let {
                vm.uploadStatus(context, it)
            }
            
        }



        Scaffold(
            floatingActionButton = {
                Fab {
                    launcher.launch("image/*")
                }
            },
            content = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(it)
                ) {
                        CommonTextChat("Status")
                    if (statuses.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = "No Statuses Available" , fontSize = 20.sp)
                        }
                    } else {
                        if (myStatuses.isNotEmpty()) {
                            CommonRow(
                                imageUrl = myStatuses[0].user.imageUrl!!,
                                name = myStatuses[0].user.name!!
                            ) {
                                navigateTO(
                                    navController,
                                    DestinationScreens.SingleStatus.createRoute(myStatuses[0].user.userID!!)
                                )
                            }
                        }
                        commonDivider()
                        val uniqueUsers = otherStatuses.map { it.user }.toSet().toList()
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(uniqueUsers) { otherUser ->
                                CommonRow(imageUrl = otherUser.imageUrl!!, name = otherUser.name!!) {
                                    navigateTO(
                                        navController,
                                        DestinationScreens.SingleStatus.createRoute(otherUser.userID!!)
                                    )

                                }
                            }
                        }

                    }

                }
            }
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 5.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            BottomNavMenu(
                selectedItem = BottomNavigationItems.STATUSLIST,
                navController = navController
            )
        }
    }

}

@Composable
fun Fab(onFabClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onFabClick.invoke() },
        containerColor = MaterialTheme.colorScheme.secondary,
        shape = CircleShape,
        modifier = Modifier.padding(bottom = 40.dp)
    )
    {
        Icon(
            imageVector = Icons.Rounded.Edit,
            contentDescription = "Add Status",
            tint = Color.White
        )


    }

}