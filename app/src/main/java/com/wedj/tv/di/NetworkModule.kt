package com.wedj.tv.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.wedj.tv.MainActivity
import com.wedj.tv.MainActivity.Companion.context
import com.wedj.tv.R
import com.wedj.tv.data.remote.RetrofitService
import com.wedj.tv.util.Config
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.concurrent.TimeUnit
import javax.net.ssl.*

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private val baseUrl = Config.BASE_URL

    @Provides
    fun provideHTTPLoggingInterceptor(): HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return interceptor
    }

    @Provides
    fun provideOkHttpClient(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor).connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(okHttpClient)
            .build()
    }

    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Provides
    fun provideRetrofitService(retrofit: Retrofit): RetrofitService =
        retrofitSSLModelBuilder().create(RetrofitService::class.java)



    fun retrofitSSLModelBuilder(): Retrofit {
/*
        // loading CAs from an InputStream
        val cf = CertificateFactory.getInstance("X.509")
        val cert = context.resources.openRawResource(R.raw.timeinator)
        val ca: Certificate
        try {
            ca = cf.generateCertificate(cert)
        } finally {
            cert.close()
        }

        // creating a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", ca)

        // creating a TrustManager that trusts the CAs in our KeyStore
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
        tmf.init(keyStore)

        // creating an SSLSocketFactory that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, tmf.getTrustManagers(), null)


*/

        val certificateFactory = CertificateFactory.getInstance("X.509")

        val inputStream =  context!!.resources.openRawResource(R.raw.wedj) //(.crt)

        val certificate = certificateFactory.generateCertificate(inputStream)
        inputStream.close()


        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)


        // Create a TrustManager that trusts the CAs in our KeyStore.
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()

        val trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        trustManagerFactory.init(keyStore)


        val trustManagers = trustManagerFactory.trustManagers
        val x509TrustManager = trustManagers[0] as X509TrustManager
        // Create an SSLSocketFactory that uses our TrustManager
        val sslContext = SSLContext.getInstance("TLS")
        // sslContext.init(null, arrayOf<TrustManager>(x509TrustManager), null)
        sslContext.init(null, trustManagers, null)
        // val sslContext = getSSLConfig()
        val clientBuilder = OkHttpClient.Builder()

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        var sslSocketFactory = sslContext.socketFactory

        clientBuilder.readTimeout(60, TimeUnit.SECONDS)
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .sslSocketFactory(sslSocketFactory, x509TrustManager)
            .retryOnConnectionFailure(true)
            .addInterceptor(interceptor)

        //   val clientBuilder = trustCert()

        clientBuilder.hostnameVerifier(object : HostnameVerifier {
            override fun verify(hostname: String, session: SSLSession): Boolean {

                return true
            }
        })
        val client = clientBuilder.build()

        /*val certificateFactory = CertificateFactory.getInstance("X.509")

        val inputStream = context.resources.openRawResource(R.raw.time) //(.crt)
        val certificate = certificateFactory.generateCertificate(inputStream)
        inputStream.close()

        // Create a KeyStore containing our trusted CAs
        val keyStoreType = KeyStore.getDefaultType()
        val keyStore = KeyStore.getInstance(keyStoreType)
        keyStore.load(null, null)
        keyStore.setCertificateEntry("ca", certificate)

        // Create a TrustManager that trusts the CAs in our KeyStore.
        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
        val trustManagerFactory = TrustManagerFactory.getInstance(tmfAlgorithm)
        trustManagerFactory.init(keyStore)

        val trustManagers = trustManagerFactory.trustManagers
        val x509TrustManager = trustManagers[0] as X509TrustManager


        // Create an SSLSocketFactory that uses our TrustManager
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf<TrustManager>(x509TrustManager), null)
        var sslSocketFactory = sslContext.socketFactory

        *//*    //create Okhttp client
            val client = OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, x509TrustManager)
                    .build()*//*


        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).sslSocketFactory(sslSocketFactory, x509TrustManager)
                .retryOnConnectionFailure(true).addInterceptor(interceptor).build()*/
        //  .baseUrl("https://api.github.com/")


//"https://cherrypickprices.com/beta/cpp/services/"
        return Retrofit.Builder()
            .client(client)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }

//
//    @Singleton
//    @Provides
//    fun provideCharacterRemoteDataSource(characterService: CharacterService) = CharacterRemoteDataSource(characterService)
//
//    @Singleton
//    @Provides
//    fun provideDatabase(@ApplicationContext appContext: Context) = AppDatabase.getDatabase(appContext)
//
//    @Singleton
//    @Provides
//    fun provideCharacterDao(db: AppDatabase) = db.characterDao()
//
//    @Singleton
//    @Provides
//    fun provideRepository(remoteDataSource: CharacterRemoteDataSource,
//                          localDataSource: CharacterDao) =
//        CharacterRepository(remoteDataSource, localDataSource)


}