package com.m3ns1.rampup.ui

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.m3ns1.rampup.Names
import com.m3ns1.rampup.R
import com.m3ns1.rampup.componentWithin
import javax.inject.Inject
import javax.inject.Named

/**
 * Created by m3ns1 on 03.12.17.
 */
class MFragment : Fragment() {

    companion object {
        fun newFragment(): Fragment {
            return MFragment()
        }
    }

    @Inject
    @field:Named(Names.APPLICATION_VERSION)
    lateinit var appVersion: String

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        componentWithin(context)?.inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater?.inflate(R.layout.fragment_main, container, false)?.let { view ->
            view.findViewById<TextView>(R.id.tv_appversion).text = "Version: $appVersion"
            view
        } ?: super.onCreateView(inflater, container, savedInstanceState)
    }
}