package com.ladrope.venpix.controller


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.ladrope.venpix.R


class FragmentThree:Fragment() {



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.ca_add_choose_plan, container, false)


        return view
    }

}