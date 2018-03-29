package tech.alvarez.collecting

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkButton.setOnClickListener {
            checkPermission()
        }

        contactsTextView.movementMethod = ScrollingMovementMethod()

        getContactsButton.setOnClickListener {
            getContactList()
        }
    }

    fun checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            messageTextView.text = "You don't have permission"

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                messageTextView.text = "You already request permission"
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 777)
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 777)
            }

        } else {
            messageTextView.text = "You already have permission"
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            777 -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    messageTextView.text = "Now you have permission"
                    getContactList()
                } else {
                    messageTextView.text = "You didn't give permission"
                }
                return
            }
        }
    }

    private fun getContactList() {
        var contacts = ""
        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cursor?.count ?: 0 > 0) {
            while (cursor != null && cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                if (cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            arrayOf(id), null)
                    while (pCur!!.moveToNext()) {
                        val phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        contacts = contacts + "\n" + name + ": " + phoneNo

                    }
                    pCur.close()
                }
            }
        }
        cursor?.close()

        contactsTextView.text = contacts
    }
}
