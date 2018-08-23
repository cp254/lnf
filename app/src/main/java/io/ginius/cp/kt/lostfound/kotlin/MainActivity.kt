package io.ginius.cp.kt.lostfound.kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import butterknife.BindView
import io.ginius.cp.kt.lostfound.kotlin.api.ApiService
import io.reactivex.disposables.Disposable
import android.util.Log
import android.view.View
import android.widget.*
import io.ginius.cp.kt.lostfound.R
import io.ginius.cp.kt.lostfound.kotlin.api.post.SearchDoc
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.content_main.*
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    private var disposable: Disposable? = null
    @BindView(R.id.searchView)
    lateinit var schView: SearchView
//    @BindView(R.id.rv)
//    lateinit var recyclerView: RecyclerView
    @BindView(R.id.button)
    lateinit var foundId: RelativeLayout
    @BindView(R.id.tv_desc)
    lateinit var tvDesc: TextView

    private val mData2 = ArrayList<SearchDoc.Result>()


    private val ApiServe by lazy {
        ApiService.create()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.content_main)
//
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//            override fun onQueryTextSubmit(query: String): Boolean {
//                //Task HERE
//                submitData()
//                return false
//            }
//
//        })



    }


    fun setupRecycler(docList: SearchDoc.Response) {
//        rv.setHasFixedSize(true)
//        val layoutManager = LinearLayoutManager(this)
//        layoutManager.orientation = LinearLayoutManager.VERTICAL
//        rv.layoutManager = layoutManager
//        rv.adapter = DocAdapter(docList.result){
//            //Log.v("Article", it.id.toString())
//        }
    }

    fun submitData() {
        val searchRequest: io.ginius.cp.kt.lostfound.kotlin.api.post.SearchDoc.Request
        val searchData: io.ginius.cp.kt.lostfound.kotlin.api.post.SearchDoc.Data = io.ginius.cp.kt.lostfound.kotlin.api.post.SearchDoc.Data(searchView.toString())
        searchRequest = io.ginius.cp.kt.lostfound.kotlin.api.post.SearchDoc.Request("search_documents_by_unique_ref", searchData)
        Toast.makeText(this, "Loading ..", Toast.LENGTH_SHORT).show()
        disposable = ApiServe.searchDoc(searchRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { result ->

                            tv_desc.visibility = View.GONE
                            rv.visibility = View.VISIBLE
                            setupRecycler(result)
                            Toast.makeText(this, result.statusname, Toast.LENGTH_LONG).show()
//                            Log.e("success", result.result.get(1)._
                            println(result.result)
                            var i = 0
                            while (i < result.result.size){
                                setupRecycler(result)

                                i++
                            }
//                            val adapter = DocAdapter(mData2)
//                            recyclerView.adapter = adapter
                        },
                        { error ->
                            Log.e("error",  error.toString())
                            //Toast.makeText(this, error.message, Toast.LENGTH_SHORT).show()
                        }
                )

    }

//    inner class searchedDocumentAdapter : RecyclerView.Adapter<searchedDocumentAdapter.DocViewHolder>() {
//
//        private val docs: MutableList<SearchDoc.Result> = mutableListOf()
//
//        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DocViewHolder {
//            return DocViewHolder(layoutInflater.inflate(R.layout.search_item, parent, false))
//        }
//
//        override fun getItemCount(): Int {
//            return docs.size
//        }
//
//        override fun onBindViewHolder(holder: DocViewHolder, position: Int) {
//            holder.bindModel(docs[position])
//        }
//
//        fun setMovies(data: List<SearchDoc.Result>) {
//            docs.addAll(data)
//            notifyDataSetChanged()
//        }
//
//        inner class DocViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//
//            val movieTitleTxt : TextView = itemView.findViewById(R.id.doc_id)
//            val movieGenreTxt : TextView = itemView.findViewById(R.id.doc_name)
//            val movieYearTxt : TextView = itemView.findViewById(R.id.doc_type)
//            val movieAvatarImage : ImageView = itemView.findViewById(R.id.hgy)
//
//            fun bindModel(doc: SearchDoc.Result) {
//                movieTitleTxt.text = doc.title
//                movieGenreTxt.text = doc.genre
//                movieYearTxt.text = doc.year
//                Picasso.get().load(doc.poster).into(movieAvatarImage)
//            }
//        }
//    }


    // abstract inner class Car internal constructor(private val name: String)


}
