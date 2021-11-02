package com.example.kotlincoroutines

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivityTag"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*
        coroutineIntroduction()

        suspendFunctions()

        coroutineContext()

        runBlocking()

        jobsWaitingCancellation()

        withTimeOut()

        async_await()
         */

        scope()
    }

    private fun coroutineIntroduction() {
        GlobalScope.launch {
            Log.d(TAG, "Coroutine says hello from thread : ${Thread.currentThread().name}")
        }
        Log.d(TAG, "Hello from thread : ${Thread.currentThread().name}")
    }

    private fun suspendFunctions() {
        GlobalScope.launch {
            val networkCallAnswer = doNetworkCall()
            val serviceCallAnswer = doServiceCall()

            Log.d(TAG, networkCallAnswer)
            Log.d(TAG, serviceCallAnswer)
        }
    }

    private fun coroutineContext() {
        GlobalScope.launch(Dispatchers.IO) {
            Log.d(TAG, "Thread: ${Thread.currentThread().name}")
            val answer = doNetworkCall()

            withContext(Dispatchers.Main) {
                tv_dummy.text = answer
                Log.d(TAG, "Thread: ${Thread.currentThread().name}")
            }
        }
    }

    private fun runBlocking() {
        Log.d(TAG, "Before runBlocking")
        runBlocking {
            launch(Dispatchers.IO) {
                delay(3000L)
                Log.d(TAG, "Ended launch IO 1")
            }

            launch(Dispatchers.IO) {
                delay(3000L)
                Log.d(TAG, "Ended launch IO 2")
            }

            Log.d(TAG, "Started runBlocking")
            delay(5500L)
            Log.d(TAG, "Ended runBlocking")
        }
        Log.d(TAG, "After runBlocking")
    }

    private fun jobsWaitingCancellation() {
        val job = GlobalScope.launch(Dispatchers.Default) {
            repeat(5) {
                Log.d(TAG, "Coroutine still working")
                delay(1000L)
            }

            Log.d(TAG, "Started Long running calculation")
            for (i in 30..40) {
                if (isActive) {
                    Log.d(TAG, "Result for i = $i = ${fib(i)}")
                }
            }
            Log.d(TAG, "Ended long running calculation ... ")
        }
        runBlocking {
            //job.join()
            delay(100L)
            job.cancel()

            Log.d(TAG, "Cancelled job !! ... ")
        }
    }

    private fun withTimeOut() {
        Log.d(TAG, "Started Long running calculation")
        GlobalScope.launch(Dispatchers.Default) {
            withTimeout(3000L) {
                for (i in 30..60) {
                    if (isActive) {
                        Log.d(TAG, "Result for i = $i = ${fib(i)}")
                    }
                }
            }
            Log.d(TAG, "Ended long running calculation ... ")
        }
    }

    @DelicateCoroutinesApi
    private fun async_await() {
        GlobalScope.launch(Dispatchers.IO) {
            val time = measureTimeMillis {
                val ans1 = doNetworkCall()
                val ans2 = doServiceCall()

                Log.d(TAG, "Answer 1 is $ans1")
                Log.d(TAG, "Answer 2 is $ans2")
            }

            Log.d(TAG, "Request took $time ms") // approx 6 secs
        }

        GlobalScope.launch {
            val time = measureTimeMillis {
                val network = async { doNetworkCall() }
                val service = async { doServiceCall() }

                Log.d(TAG, "Answer 1 is ${network.await()}")
                Log.d(TAG, "Answer 2 is ${service.await()}")
            }

            Log.d(TAG, "Request took $time ms")
        }
    }

    private fun scope() {
        btn_start_activity.setOnClickListener {
            GlobalScope.launch {
                while (true) {
                    delay(1000L)
                    Log.d(TAG, "Still running ... ")
                }
            }

            GlobalScope.launch {
                delay(5000L)
                Intent(
                    this@MainActivity,
                    SecondActivity::class.java
                ).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        /** the above code shows that even after another activity starts, this
             coroutines still goes on and on ..
         */

        lifecycleScope.launch {
            while (true) {
                delay(1000L)
                Log.d(TAG, "Still running ... ")
            }
        }

        GlobalScope.launch {
            delay(5000L)
            Intent(
                this@MainActivity,
                SecondActivity::class.java
            ).also {
                startActivity(it)
                finish()
            }
        }
    }

    private suspend fun doNetworkCall(): String {
        delay(3000L)
        return "returned answer of network call"
    }

    private suspend fun doServiceCall(): String {
        delay(3000L)
        return "returned answer of service call"
    }

    private fun fib(n: Int): Long {
        return when (n) {
            0 -> 0
            1 -> 1
            else -> fib(n-1) + fib(n-2)
        }
    }
}