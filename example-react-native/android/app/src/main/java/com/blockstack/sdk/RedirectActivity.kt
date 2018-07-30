package com.blockstack.sdk

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.facebook.react.bridge.Arguments
import org.blockstack.android.sdk.BlockstackSession

class RedirectActivity : AppCompatActivity() {
    private lateinit var _blockstackSession: BlockstackSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }
    }

    private fun handleAuthResponse(intent: Intent) {
        val response = intent.dataString
        if (response != null) {
            val authResponseTokens = response.split(':')

            if (authResponseTokens.size > 1) {
                val authResponse = authResponseTokens[1]

                if (BlockstackNativeModule.currentSession != null) {
                    BlockstackNativeModule.currentSession!!.handlePendingSignIn(authResponse, { userData ->
                        if (userData.hasValue) {
                            // The user is now signed in!
                            runOnUiThread {
                                Log.d("RedirectActivity", "user logged in")
                                if (BlockstackNativeModule.currentSignInPromise != null) {
                                    val map = Arguments.createMap()
                                    map.putString("decentralizedID", userData.value!!.decentralizedID)
                                    BlockstackNativeModule.currentSignInPromise!!.resolve(map)
                                }
                                finish()
                            }
                        }
                    })
                }
            }
        }
    }

    fun blockstackSession(): BlockstackSession {
        val session = _blockstackSession
        if (session != null) {
            return session
        } else {
            throw IllegalStateException("No session.")
        }
    }
}