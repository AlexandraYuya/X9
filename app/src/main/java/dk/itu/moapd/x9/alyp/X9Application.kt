package dk.itu.moapd.x9.alyp

import android.app.Application

/**
 * Application.onCreate() called by system when application is first loaded into memory i.e. 'launches' and destroyed when X9 process is destroyed.
 * Not re-created upon configuration changes.
 * Good place to do any kind of one-time initialization operations
 */
class X9Application : Application() {
    override fun onCreate() {
        super.onCreate()
        ReportRepository.initialize(this)
    }
}