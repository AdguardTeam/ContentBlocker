/*
 This file is part of AdGuard Content Blocker (https://github.com/AdguardTeam/ContentBlocker).
 Copyright © 2018 AdGuard Content Blocker. All rights reserved.

 AdGuard Content Blocker is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by the
 Free Software Foundation, either version 3 of the License, or (at your option)
 any later version.

 AdGuard Content Blocker is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

 You should have received a copy of the GNU General Public License along with
 AdGuard Content Blocker.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.adguard.android.contentblocker;

import android.app.job.JobParameters;
import android.app.job.JobService;

import com.adguard.android.contentblocker.service.FilterService;

public class FilterUpdateJobService extends JobService {

    private FilterService filterService;

    @Override
    public void onCreate() {
        super.onCreate();
        filterService = ServiceLocator.getInstance(getApplicationContext()).getFilterService();
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {

        filterService.tryUpdateFilters();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        jobFinished(jobParameters, true);
        return true;
    }
}
