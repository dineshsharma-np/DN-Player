package com.example.dnplayer
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)


        val nowPlayingFrame = view.findViewById<FrameLayout>(R.id.nowPlayingHome)

        // Set click listener
        nowPlayingFrame.setOnClickListener {
            // Start PrivacyPolicyActivity
            val intent = Intent(requireContext(), PlayerActivity::class.java)
            startActivity(intent)
        }



        return view
    }

}
