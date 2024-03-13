package com.afif.lockscreen

import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.compose.rememberNavController
import com.plcoding.composemultiselect.ui.theme.ComposeMultiSelectTheme

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PermissionApps.newInstance] factory method to
 * create an instance of this fragment.
 */
class PermissionsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var permissionsInfoArray:Array<PermInfo>? = null
    private lateinit var formatedIndexedPermsMap:Map<Int,PermStat>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        val deviceChecker = DeviceChecker()

        //etName.shouldShowSoftInputOnFocus(false)

        var permissionsInfoList = deviceChecker.getRequestedPermissionsStatus(context)?.toList()
        permissionsInfoArray = permissionsInfoList?.toTypedArray()
        Log.d("permissionsInfoArray","permissionsInfoArray type "+permissionsInfoArray?.size)


        var permissions: MutableList<String>? = mutableListOf()

        permissionsInfoList?.forEach{item ->
            permissions?.add(item.permissionName)

        }

        var formatedPerms = permissions?.groupingBy { it }?.eachCount()?.filter { it.value > 1 }

        formatedPerms?.let { it ->
            formatedIndexedPermsMap = it.entries.mapIndexed { index, entry ->
                index to PermStat(entry.key, entry.value)
            }.toMap()
        }



        Log.d("lifecycle","PermissionsFragment onCreate ")

    }

    @RequiresApi(33)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("lifecycle","PermissionsFragment onCreateView ")




        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_permission_apps, container, false)
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
//                MaterialTheme {
//                    // In Compose world
//                    Text("Hello Compose!")
//                }

                var navController = rememberNavController()
                navController = view?.let { Navigation.findNavController(it) } as NavHostController;

                ComposeMultiSelectTheme {

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ){
                        formatedIndexedPermsMap?.size?.let {

                            items(it){ i ->
                                var permissionInfo = formatedIndexedPermsMap[i]
                                var perNameArr = permissionInfo?.name.toString().split('.')
                                var perName = perNameArr[perNameArr.size-1]+" : "+formatedIndexedPermsMap[i]?.count
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

                                            val navAction = PermissionsFragmentDirections.actionPermissionsFragmentToPermissionAppsFragment(permissionsInfoArray,permissionInfo?.name)
                                            navController.navigate(navAction)
                                        }

                                        .padding(30.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ){


                                    Text(text = perName)
                                }

                            }
                        }
                    }

                }

            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PermissionApps.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PermissionsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}