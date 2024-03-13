package com.afif.lockscreen

import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Space
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.plcoding.composemultiselect.ui.theme.ComposeMultiSelectTheme
import androidx.navigation.compose.rememberNavController
class KotActivity : AppCompatActivity() {

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kot)


        //val deviceChecker = DeviceChecker()

        //etName.shouldShowSoftInputOnFocus(false)

//        var permissionsInfoList = deviceChecker.getRequestedPermissionsStatus(this)
//
//
//        var permissions: MutableList<String>? = mutableListOf()
//
//        permissionsInfoList?.forEach{item ->
//            permissions?.add(item.permissionName)
//
//        }
//
//        var formatedPerms = permissions?.groupingBy { it }?.eachCount()?.filter { it.value > 1 }
//        val formatedIndexedPermsMap = formatedPerms?.entries?.mapIndexed { index, entry ->
//            index to PermissionInfo(entry.key, entry.value)
//        }?.toMap()
//
//        Log.d("permissions_ui","formatedPermissions "+formatedPerms)
//
//
//        //if (permissions != null) {
//            Log.d("permissions_ui","permissionsl size:"+permissionsInfoList?.size)
        //supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,PermissionApps())

           // setContent{
            //    val navController = rememberNavController()
//
//                ComposeMultiSelectTheme {
//                    var items by remember {
//                        mutableStateOf(
//                            (1..20).map {
//                                ListItem(
//                                    title = "Item $it",
//                                    isSelected = false
//                                )
//                            }
//                        )
//
//                    }
//
//                    items.filter { it.isSelected }
//                    LazyColumn(
//                        modifier = Modifier
//                            .fillMaxSize()
//                    ){
//                        formatedIndexedPermsMap?.size?.let {
//                            items(it){ i ->
//                                Row(
//                                    modifier = Modifier
//                                        .fillMaxWidth()
//                                        .clickable {
//                                            //                                        items = items.mapIndexed { j,item ->
//                                            //                                            if(i == j){
//                                            //                                               item.copy(isSelected = !item.isSelected)
//                                            //                                            }else item
//                                            //
//                                            //                                        }
//
//                                            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,PermissionApps())
//
//
//                                        }
//                                        .padding(30.dp),
//                                    horizontalArrangement = Arrangement.SpaceBetween,
//                                    verticalAlignment = Alignment.CenterVertically
//                                ){
//                                    var perNameArr = formatedIndexedPermsMap[i]?.name.toString().split('.')
//                                    var perName = perNameArr[perNameArr.size-1]+" : "+formatedIndexedPermsMap[i]?.count
//
//                                    Text(text = perName)
////                                    if(items[i].isSelected){
////                                        Icon(
////                                            imageVector = Icons.Default.Check,
////                                            contentDescription = "Selected",
////                                            tint = Color.Green,
////                                            modifier = Modifier.size(20.dp)
////                                        )
////                                    }
//
//                                }
//
//                            }
//                        }
//                    }
//
//                }

                //PermissionsList(permissions)
         //   }

        //}
    }


    //@Composable
//    fun PermissionsList(permissions: MutableList<PermInfo>) {
//
//        LazyColumn(){
//            items(permissions.size) { i ->
//                Row (modifier = Modifier
//                    .fillMaxWidth()
//                    .clickable {
//                        items = items.mapIndexed {
//
//                        }
//
//                    }
//                    .padding(16.dp),
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    verticalAlignment = Alignment.CenterVertically
//                ){
//                    Text(text = "permissions[i].permissionName")
//                    if (permissions[i].isGranted){
//                        Icon(
//                            imageVector =  Icons.Default.Check,
//                            contentDescription = "granted",
//                            tint = Color.Green,
//                            modifier = Modifier.size(20.dp)
//
//                        )
//                    }
//                }
//
//            }
//        }
//
//    }
}