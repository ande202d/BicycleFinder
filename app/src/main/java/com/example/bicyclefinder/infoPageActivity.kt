package com.example.bicyclefinder

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_bicycle_info.*
import java.util.*

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class infoPageActivity : AppCompatActivity() {

    companion object{
        const val BIKE = "BIKE" //they are already public
        const val CURRENTUSER = "CURRENTUSER"
    }

    //private val LOG_TAG = "MINE"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bicycle_info)

        val currentBike = intent.getSerializableExtra(BIKE) as? Bike
        val currentUser = intent.getSerializableExtra(CURRENTUSER) as? User

        //val intentBike = intent.getSerializableExtra(BIKE) as? Bike
        //val intentUser = intent.getSerializableExtra(CURRENTUSER) as? User
        //if (intentBike != null) currentBike = intentBike
        //if (intentUser != null) currentUser = intentUser

        if (currentBike != null) {
            FillWithBikeData(currentBike)
            FillWithUserData(currentBike.userId)
        }

        infoDeleteButton.setOnClickListener {
            DeleteBike(currentBike)
        }

        infoDeleteButton.visibility = View.GONE
        if (currentUser != null){
            if (currentUser.id.equals(currentBike?.userId)) infoDeleteButton.visibility = View.VISIBLE
        }
    }

    fun FillWithBikeData(b: Bike?){
        infoId.text = b?.id.toString()
        infoFrameNumber.text = b?.frameNumber
        infoKindOfBicycle.text = b?.kindOfBicycle
        infoBrand.text = b?.brand
        infoColors.text = b?.colors
        infoPlace.text = b?.place
        infoDate.text = b?.date
        infoUserId.text = b?.userId.toString()
        infoMissingFound.text = b?.missingFound
    }
    fun FillWithUserData(id: Int){
        val call = ApiUtils.getInstance().restService.getOneUser(id)
        call.enqueue(object : Callback<User>{
            override fun onFailure(call: Call<User>, t: Throwable) {
                //Toast.makeText(this@infoPageActivity, "", Toast.LENGTH_SHORT).show()
                Toast.makeText(baseContext, ""+ t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful && response.body() != null){
                    val u = response.body()
                    infoIdUser.text = u?.id.toString()
                    infoNameUser.text = u?.name
                    infoPhoneUser.text = u?.phone
                    infoFirebaseIdUser.text = u?.firebaseUserId
                } else if (response.isSuccessful){
                    Toast.makeText(baseContext, "User not found in REST", Toast.LENGTH_LONG).show()
                }
                else {
                    Toast.makeText(baseContext, "" + response.message(), Toast.LENGTH_LONG).show()
                }
            }

        })
    }
    fun DeleteBike(b: Bike?){

        if (b != null){
            ApiUtils.getInstance().restService.deleteBike(b.id as Int).enqueue(object : Callback<String>{
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Toast.makeText(baseContext, "" + t.message, Toast.LENGTH_LONG).show()
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        Toast.makeText(baseContext, "Successfully deleted: " + b.id, Toast.LENGTH_LONG).show()
                        finish()
                    }
                    else Toast.makeText(baseContext, "ID: " + b.id + ", ERROR: " + response.message(), Toast.LENGTH_LONG).show()
                }

            })
        }
    }


}