package com.ikramjamro.chitchat.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.ikramjamro.chitchat.DestinationScreens
import com.ikramjamro.chitchat.R
import com.ikramjamro.chitchat.navigateTO

enum class BottomNavigationItems(val icon: Int , val navDestination: DestinationScreens ){

    CHATLIST(R.drawable.chatlisticon, DestinationScreens.ChatList),
    STATUSLIST(R.drawable.status,DestinationScreens.StatusList),
    PROFILE(R.drawable.profile,DestinationScreens.Profile)

}

@Composable
fun BottomNavMenu(
     selectedItem: BottomNavigationItems,
     navController: NavController
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 4.dp)
            .background(Color.White)
    ) {

        for(items in BottomNavigationItems.values()){

            Image(painter = painterResource(id = items.icon), contentDescription = null,
                modifier = Modifier.padding(4.dp)
                    .size(35.dp)
                    .weight(1f)
                    .clickable {
                          navigateTO(navController,items.navDestination.route)
                    },
                  colorFilter =   if(items==selectedItem){
                         ColorFilter.tint(Color.Black)
                } else {
                    ColorFilter.tint(Color.Gray)
                }
            )

        }

    }

}