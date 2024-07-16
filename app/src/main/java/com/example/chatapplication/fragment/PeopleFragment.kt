package com.example.chatapplication.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapplication.R
import com.example.chatapplication.User
import com.example.chatapplication.databinding.FragmentPeopleBinding

class PeopleFragment : Fragment() {
    lateinit var binding: FragmentPeopleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = FragmentPeopleBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_people, container, false)
        return view
    }

    companion object {
        fun newInstance() : PeopleFragment {
            return PeopleFragment()
        }
    }
}