package com.afif.lockscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Animatable
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavArgs
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.fragment.navArgs
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.pagerTabIndicatorOffset
import com.google.accompanist.pager.rememberPagerState
import com.plcoding.composemultiselect.ui.theme.ComposeMultiSelectTheme
import com.plcoding.composemultiselect.ui.theme.Denim
import com.plcoding.composemultiselect.ui.theme.Malibu
import kotlinx.coroutines.launch


private const val ARG_PARAM1 = "PERMISSIONS"
private const val ARG_PARAM2 = "PERMNAME"


class PermissionAppsFragment : Fragment() {
    private val args by navArgs<PermissionAppsFragmentArgs>()
    private var permissionsInfoArr: Array<PermInfo>? = null
    private var permName: String? = null
    var th3PAppsPermsInfoList: MutableList<PermInfo>? = mutableListOf()
    var systemAppsPermsInfoList: MutableList<PermInfo>? = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("lifecycle","PermissionAppsFragment onCreate ")

        arguments?.let {
            permissionsInfoArr = args.permissions
            permName = args.permName
        }

        permissionsInfoArr?.forEach{item ->
            if(item.permissionName == permName){
                if(item.isSystemApp){
                    systemAppsPermsInfoList?.add(item)
                }else{
                    th3PAppsPermsInfoList?.add(item)
                }
            }


        }

    }

    @SuppressLint("RememberReturnType")
    @OptIn(ExperimentalPagerApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("lifecycle","PermissionAppsFragment onCreateView ")

        // Inflate the layout for this fragment
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {

                val tabItems = listOf("3DP","System")
                val pagerState = rememberPagerState()
                val couroutineScope = rememberCoroutineScope()

                var navController = rememberNavController()
                navController = view?.let { Navigation.findNavController(it) } as NavHostController;
                ComposeMultiSelectTheme {

                    // TabsJetpackComposeExampleTheme
                    Surface() {
                        Column {

                            TabRow(selectedTabIndex = pagerState.currentPage,
                                backgroundColor = Malibu,
                                modifier = Modifier
                                    .padding(all = 20.dp)
                                    .background(color = Color.Transparent)
                                    .clip(RoundedCornerShape(30.dp)),
                                indicator = { tabPositions ->
                                    TabRowDefaults.Indicator(
                                        Modifier
                                            .pagerTabIndicatorOffset(pagerState, tabPositions)
                                            .width(0.dp)
                                            .height(0.dp)
                                    )
                                }
                            )
                            {
                                tabItems.forEachIndexed { index, title ->
                                    val color = remember {

                                        Animatable(Denim)
                                    }
                                    LaunchedEffect(key1 = pagerState.currentPage == index) {
                                        color.animateTo(if (pagerState.currentPage == index) Color.White else Malibu)
                                    }

                                    Tab(
                                        text = {
                                            Text(
                                                title,
                                                style = if (pagerState.currentPage == index)
                                                    TextStyle(color = Denim, fontSize = 18.sp)
                                            else TextStyle(color = Denim, fontSize = 16.sp)
                                            )
                                        },
                                        selected = pagerState.currentPage == index,
                                        modifier = Modifier.background(color = color.value,
                                        shape = RoundedCornerShape(30.dp)),

                                        onClick = {
                                            couroutineScope.launch {
                                                pagerState.animateScrollToPage(index)

                                            }
                                        })


                                }

                            }
                            Box(
                                modifier = Modifier.fillMaxWidth(),

                                contentAlignment = Alignment.TopCenter,

                            ) {
                                permName?.let {
                                    Text(
                                        text = it,
//                                        style = MaterialTheme.typography.h6,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(30.dp),

                                //contentAlignment = Alignment.TopCenter,
                            horizontalArrangement = Arrangement.SpaceBetween,

                                ) {
                                    Text(
                                        text = "App Name",
//                                        style = MaterialTheme.typography.h6,
                                        fontWeight = FontWeight.Bold,

                                    )
                                Text(
                                    text = "Is Granted",
//                                        style = MaterialTheme.typography.h6,
                                    fontWeight = FontWeight.Bold
                                )

                            }
                            HorizontalPager(
                                count = tabItems.size,
                                state = pagerState,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(color = Color.White)
                            ) { page ->
//                                Text(
//                                    text = tabItems[page],
//                                    modifier = Modifier.padding(50.dp),
//                                    color = Color.White
//                                )




                        LazyColumn(
                           modifier = Modifier.fillMaxSize()
                        ){
                            var appsList = if (page == 0)
                            th3PAppsPermsInfoList
                            else
                                systemAppsPermsInfoList

                            appsList?.let {perInfoList ->
                                items(perInfoList.size){ it ->
                                    var permissionInfo = perInfoList[it]

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                //                                        items = items.mapIndexed { j,item ->
                                                //                                            if(i == j){
                                                //                                               item.copy(isSelected = !item.isSelected)
                                                //                                            }else item
                                                //
                                                //                                        }
                                                //                                            val args = Bundle().apply {
                                                //                                                putParcelable("PERMISSIONS", permissionsInfoList as Parcelable)
                                                //                                            }
                                                //                                            var permissionAppsFragment = PermissionAppsFragment()
                                                //                                            permissionAppsFragment.arguments = args


                                            }

                                            .padding(30.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ){



                                        Text(text = permissionInfo.packageInfo)
                                        Text(text = permissionInfo.isGranted.toString())
                                    }

                                }
                            }

                        }


                            }
                        }
                    }

                //close tabs }

                }








            }
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PermissionAppsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }
}