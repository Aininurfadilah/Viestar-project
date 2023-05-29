package com.example.viestar.ui.study

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.example.viestar.R
import com.example.viestar.ui.profile.ProfileViewModel

class StudyFragment : Fragment() {
    private lateinit var studyViewModel: StudyViewModel

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        studyViewModel =
            ViewModelProvider(this).get(StudyViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_study, container, false)
        val textView: TextView = root.findViewById(R.id.text_study)
        studyViewModel.text.observe(viewLifecycleOwner, {
            textView.text = it
        })
        return root
    }
}