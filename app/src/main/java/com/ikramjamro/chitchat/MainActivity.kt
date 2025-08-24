package com.ikramjamro.chitchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ikramjamro.chitchat.Screens.SignupScreen
import com.ikramjamro.chitchat.Screens.loginScreen
import androidx.hilt.navigation.compose.hiltViewModel
import com.ikramjamro.chitchat.Screens.ChatListScreen
import com.ikramjamro.chitchat.Screens.ProfileScreen
import com.ikramjamro.chitchat.Screens.SingleChatScreen
import com.ikramjamro.chitchat.Screens.SingleStatusScreen
import com.ikramjamro.chitchat.Screens.SplashScreen
import com.ikramjamro.chitchat.Screens.StatusScreen
import dagger.hilt.android.AndroidEntryPoint

sealed class DestinationScreens(var route: String){

    object SignUp: DestinationScreens("signup")
    object Login: DestinationScreens("login")
    object Profile: DestinationScreens("profile")
    object ChatList: DestinationScreens("chatList")
    // for dynamic routes
    object SingleChat: DestinationScreens("singleChat/{chatId}"){
        fun createRoute(id: String) : String{   return "singleChat/$id"  }
    }
    object StatusList: DestinationScreens("statusList")
    object SingleStatus: DestinationScreens("singleStatus/{userID}"){
        fun createRoute(userID: String) : String { return  "singleStatus/$userID"  }
    }
    object  splash: DestinationScreens("splash")

}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
              chatAppNavigation()
        }

        }
    }

@Composable
fun chatAppNavigation(){

     var navController = rememberNavController()
     var vm= hiltViewModel<CCViewModel>()
    NavHost(navController = navController, startDestination = DestinationScreens.splash.route){
          composable(DestinationScreens.splash.route) {
              SplashScreen(navController = navController, vm =vm)
          }
         composable(DestinationScreens.SignUp.route){
             SignupScreen(navController,vm)
         }
         composable(DestinationScreens.Login.route){
             loginScreen(vm,navController)
         }
         composable(DestinationScreens.ChatList.route){
             ChatListScreen(navController,vm)
         }
         composable(DestinationScreens.SingleChat.route){
             val chatId=it.arguments?.getString("chatId")
             chatId?.let {
                 SingleChatScreen(navController = navController,vm=vm,chatId= it)
             }

         }
         composable(DestinationScreens.StatusList.route){
             StatusScreen(navController,vm)
         }
         composable(DestinationScreens.Profile.route){
             ProfileScreen(navController,vm)
         }

         composable(DestinationScreens.SingleStatus.route){
             val userID=it.arguments?.getString("userID")
             userID?.let {
                 SingleStatusScreen(navController = navController,vm=vm,userID=it)
             }

         }

     }

}
