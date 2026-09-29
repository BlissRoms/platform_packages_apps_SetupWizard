/*
 * SPDX-FileCopyrightText: 2016 The CyanogenMod Project
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.blissroms.setupwizard.settings

import android.content.Context
import android.os.Bundle
import android.os.UserHandle
import android.text.TextUtils
import lineageos.hardware.LineageHardwareManager
import lineageos.providers.LineageSettings
import org.blissroms.setupwizard.DISABLE_NAV_KEYS
import org.blissroms.setupwizard.KEY_SEND_METRICS
import org.blissroms.setupwizard.R
import org.blissroms.setupwizard.SetupWizardApp
import org.blissroms.setupwizard.base.BaseSetupWizardActivity
import org.blissroms.setupwizard.util.SetupWizardUtils

class BlissSettingsActivity : BaseSetupWizardActivity() {

    private val metricsItem by lazy { toggle(R.id.metrics_item) }
    private val navKeysItem by lazy { toggle(R.id.nav_keys_item) }

    private var supportsKeyDisabler = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val osName = SetupWizardUtils.getProjectName(this)
        setDescriptionText(buildDescription(osName))

        val metricsHelpImproveBliss = getString(R.string.services_help_improve_cm, osName)
        metricsItem.summary =
            getString(R.string.services_metrics_label, metricsHelpImproveBliss, osName, osName)
        metricsItem.setOnCheckedChangeListener { isChecked ->
            SetupWizardApp.settingsBundle.putBoolean(KEY_SEND_METRICS, isChecked)
        }

        supportsKeyDisabler = isKeyDisablerSupported(this)
        navKeysItem.isVisible = supportsKeyDisabler
        navKeysItem.setOnCheckedChangeListener { isChecked ->
            SetupWizardApp.settingsBundle.putBoolean(DISABLE_NAV_KEYS, isChecked)
        }
    }

    override fun onResume() {
        super.onResume()
        updateDisableNavkeysOption()
        updateMetricsOption()
    }

    override val layoutResId = R.layout.setup_bliss_settings

    override val titleResId = R.string.setup_services

    override val iconResId = R.drawable.logo

    override val itemEntriesResId = R.xml.bliss_settings_items

    private fun buildDescription(osName: String): CharSequence =
        TextUtils.concat(
            getString(
                R.string.services_full_description,
                getString(R.string.services_pp_explanation, osName),
                getString(R.string.services_find_privacy_policy),
            ),
            "\n\n",
            getText(R.string.services_privacy_policy_uri),
        )

    private fun updateMetricsOption() {
        val myPageBundle = SetupWizardApp.settingsBundle
        val metricsChecked =
            !myPageBundle.containsKey(KEY_SEND_METRICS) || myPageBundle.getBoolean(KEY_SEND_METRICS)
        metricsItem.isChecked = metricsChecked
        myPageBundle.putBoolean(KEY_SEND_METRICS, metricsChecked)
    }

    private fun updateDisableNavkeysOption() {
        if (supportsKeyDisabler) {
            val myPageBundle = SetupWizardApp.settingsBundle
            val enabled =
                LineageSettings.System.getIntForUser(
                    contentResolver,
                    LineageSettings.System.FORCE_SHOW_NAVBAR,
                    0,
                    UserHandle.USER_CURRENT,
                ) != 0
            val checked =
                if (myPageBundle.containsKey(DISABLE_NAV_KEYS)) {
                    myPageBundle.getBoolean(DISABLE_NAV_KEYS)
                } else {
                    enabled
                }
            navKeysItem.isChecked = checked
            myPageBundle.putBoolean(DISABLE_NAV_KEYS, checked)
        }
    }

    companion object {
        private fun isKeyDisablerSupported(context: Context): Boolean {
            val hardware = LineageHardwareManager.getInstance(context)
            return hardware.isSupported(LineageHardwareManager.FEATURE_KEY_DISABLE)
        }
    }
}
