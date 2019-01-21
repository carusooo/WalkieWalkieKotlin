package com.example.macarus0.walkiewalkie.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

import com.example.macarus0.walkiewalkie.R
import com.example.macarus0.walkiewalkie.data.Owner
import com.example.macarus0.walkiewalkie.data.Walk
import com.example.macarus0.walkiewalkie.data.WalkPhoto

import java.util.ArrayList

class WalkEmailUtil(private var mContext: Context, private var mWalk: Walk, internal var mWalkPhotos: List<WalkPhoto>,
                    private var mWalkOwners: List<Owner>) {
    val emailIntent: Intent
        get() {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_EMAIL, emailAddresses)
            intent.putExtra(Intent.EXTRA_SUBJECT, emailSubject)
            intent.putExtra(Intent.EXTRA_HTML_TEXT, emailHTML)
            intent.putExtra(Intent.EXTRA_TEXT, emailText)
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            return intent
        }

    private val imageUris: ArrayList<Uri>
        get() {
            val photoUris = ArrayList<Uri>()
            for (walkPhoto in mWalkPhotos) {
                photoUris.add(Uri.parse(walkPhoto.photoUri))
            }
            return photoUris
        }

    private val emailAddresses: Array<String>
        get() {
            val emails = arrayOfNulls<String>(mWalkOwners.size)
            var i = 0
            for (owner in mWalkOwners) {
                emails[i++] = owner.emailAddress
            }
            return emails.filterNotNull().toTypedArray()
        }

    private val emailSubject: String
        get() {
            val sb = StringBuilder()
            sb.append(String.format(mContext.getString(R.string.email_subject),
                    TimeStampUtil.getStringDate(mWalk.walkDate)))
            return sb.toString()
        }

    private val emailText: String
        get() {
            val sb = StringBuilder()
            sb.append(String.format(mContext.getString(R.string.email_text_duration_distance),
                    mWalk.walkDuration, this.walkDistance))
            sb.append(String.format("\n\n%s: %s", mContext.getString(R.string.email_map_label), mWalk.walkPathUrl))

            return sb.toString()
        }

    private val emailHTML: String
        get() {
            val sb = StringBuilder()
            val distanceString = String.format(mContext.getString(R.string.email_text_duration_distance),
                    mWalk.walkDuration, this.walkDistance)
            sb.append(String.format("<p>%s</p>", distanceString))
            sb.append(String.format("<p><a href=\"%s\">%s.</a></p>", mWalk.walkPathUrl, mContext.getString(R.string.email_map_label)))
            return sb.toString()
        }

    private val walkDistance: String
        get() = String.format(mContext.getString(R.string.email_text_distance_km), mWalk.walkDistance)


}
