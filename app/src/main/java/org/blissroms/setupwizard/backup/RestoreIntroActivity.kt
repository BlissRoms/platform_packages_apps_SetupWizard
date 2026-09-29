/*
 * SPDX-FileCopyrightText: 2019-2020 The Calyx Institute
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.blissroms.setupwizard.backup

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResult
import com.google.android.setupcompat.util.ResultCodes.RESULT_ACTIVITY_NOT_FOUND
import org.blissroms.setupwizard.ACTION_RESTORE_FROM_BACKUP
import org.blissroms.setupwizard.R
import org.blissroms.setupwizard.base.SubBaseActivity
import org.blissroms.setupwizard.util.SetupWizardUtils

class RestoreIntroActivity : SubBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDescriptionText(
            getString(R.string.intro_restore_subtitle, SetupWizardUtils.getProjectName(this))
        )
        setNextText(R.string.intro_restore_button)
    }

    override fun onSubactivityResult(activityResult: ActivityResult) {
        val resultCode = activityResult.resultCode
        val data = activityResult.data
        when {
            resultCode != RESULT_CANCELED -> nextAction(resultCode, data)
            isSubactivityNotFound -> finishAction(RESULT_ACTIVITY_NOT_FOUND)
            data?.getBooleanExtra("onBackPressed", false) == true -> onStartSubactivity()
        }
    }

    override fun onStartSubactivity() {
        setNextAllowed(true)
    }

    override fun onNextPressed() {
        launchRestore()
    }

    override val layoutResId = R.layout.intro_restore_activity

    override val titleResId = R.string.intro_restore_title

    override val iconResId = R.drawable.ic_restore

    override val installFooterBar = true

    override val showSkipButton = true

    private fun launchRestore() {
        startSubactivity(Intent(ACTION_RESTORE_FROM_BACKUP))
    }
}
