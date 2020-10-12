package com.example.kookcoching.Fragment.Information

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kookcoching.R
import com.google.firebase.firestore.FirebaseFirestore


/**
 * A simple [Fragment] subclass.
 * Use the [InformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class InformationFragment : Fragment() {

    val recyclerView : RecyclerView?= null
    var firestore : FirebaseFirestore?= null

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment InformationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            InformationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_information, container, false)
        val btn_move = view!!.findViewById(R.id.btn_moveToBoard) as Button
        // Inflate the layout for this fragment

        btn_move.setOnClickListener {
            activity?.let {
                val intent = Intent(context, WriteBoardActivity::class.java)
                startActivity(intent)
            }
        }
        firestore = FirebaseFirestore.getInstance()

        recyclerView?.adapter = RecyclerViewAdapter()
        recyclerView?.layoutManager = LinearLayoutManager(context)


        return view
    }

    inner class RecyclerViewAdapter : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){


        var postList : ArrayList<Post> = arrayListOf()

        init {
            firestore?.collection("post")?.addSnapshotListener { value, error ->
                postList.clear()

                for (i in value!!.documents){
                    var item = i.toObject(Post::class.java)
                    postList.add(item!!)
                }
                notifyDataSetChanged()
            }
        }


        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val title = itemView.findViewById<TextView>(R.id.cardView_title)
            val content = itemView.findViewById<TextView>(R.id.cardView_content)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            var view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.title.text = postList[position].title
            holder.content.text = postList[position].content

        }

        override fun getItemCount(): Int {
            return postList.size
        }
    }

}