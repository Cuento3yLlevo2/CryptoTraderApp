package com.platzi.android.firestore.network

import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User

const val CRYPTOS_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"

class FirestoreService(val firebaseFirestore: FirebaseFirestore) {

    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Void>){
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener { callback.onSuccess(null) }
            .addOnFailureListener { exception -> callback.onFailed(exception)}
    }

    fun updateUser(user: User, callback: Callback<Void>){
        user.name?.let {
            firebaseFirestore.collection(USERS_COLLECTION_NAME).document(it)
                .update("cryptoList", user.cryptoList)
                .addOnSuccessListener { callback.onSuccess(null)}
                .addOnFailureListener { e -> callback.onFailed(e) }
        }
    }

    fun updateCrypto(crypto: Crypto, callback: Callback<Void>){
        crypto.id?.let {
            firebaseFirestore.collection(CRYPTOS_COLLECTION_NAME).document(it)
                .update("available", crypto.available)
                .addOnSuccessListener { callback.onSuccess(null)}
                .addOnFailureListener { e -> callback.onFailed(e) }
        }
    }


}