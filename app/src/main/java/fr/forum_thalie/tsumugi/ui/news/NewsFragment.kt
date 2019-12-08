package fr.forum_thalie.tsumugi.ui.news

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import fr.forum_thalie.tsumugi.R

class NewsFragment : Fragment() {

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var root = inflater.inflate(R.layout.fragment_news, container, false) as View
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
        {
            root = inflater.inflate(R.layout.fragment_news, container, false) as androidx.coordinatorlayout.widget.CoordinatorLayout
            activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            val webView = WebView(context)

            newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel::class.java)

            if (!newsViewModel.isWebViewLoaded)
            {
                try {
                    val webViewS = root.findViewById<WebView>(R.id.news_webview)
                    val webViewChat = WebViewNews(webViewS as WebView)
                    webViewChat.start()
                } catch (e: Exception) {
                    root = inflater.inflate(R.layout.fragment_error_chat, container, false)
                }

                newsViewModel.isWebViewLoaded = true
                Log.d(tag, "webview created")
            } else {
                Log.d(tag, "webview already created!?")
            }

            return root
        }

        newsViewModel =
                ViewModelProviders.of(this).get(NewsViewModel::class.java)

        root = inflater.inflate(R.layout.fragment_news, container, false) as androidx.swiperefreshlayout.widget.SwipeRefreshLayout

        viewManager = LinearLayoutManager(context)
        viewAdapter = NewsAdapter(newsViewModel.newsArray, context!!)
        recyclerView = root.findViewById<RecyclerView>(R.id.news_recycler).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        root.setOnRefreshListener {

            newsViewModel.fetch(root, viewAdapter)

        }

        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        newsViewModel =
            ViewModelProviders.of(this).get(NewsViewModel::class.java)

        newsViewModel.fetch()
        Log.d(tag, "news fetched onCreate")
        super.onCreate(savedInstanceState)
    }
}