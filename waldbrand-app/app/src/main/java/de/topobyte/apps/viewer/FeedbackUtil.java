// Copyright 2021 Sebastian Kuerten
//
// This file is part of waldbrand-app.
//
// waldbrand-app is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// waldbrand-app is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with waldbrand-app. If not, see <http://www.gnu.org/licenses/>.

package de.topobyte.apps.viewer;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import de.waldbrandapp.R;

public class FeedbackUtil
{

  private static final String MAILTO_FEEDBACK = "team@waldbrand-app.de";

  public static void sendFeedbackMail(Context context)
  {
    String subject = context.getString(R.string.app_name);
    sendEmail(context, MAILTO_FEEDBACK, subject, "");
  }

  public static void sendEmail(Context context, String recipient,
                               String subject, String message)
  {
    try {
      String uriText = "mailto:" + recipient + "?subject="
          + Uri.encode(subject) + "&body=" + Uri.encode(message);

      Uri uri = Uri.parse(uriText);

      Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
      sendIntent.setData(uri);
      context.startActivity(sendIntent);
    } catch (ActivityNotFoundException e) {
      // cannot send email for some reason
    }
  }

  public static void share(Context context)
  {
    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
    sharingIntent.setType("text/plain");
    String using = context.getString(R.string.share_using);
    String subject = context.getString(R.string.app_name);

    String packageName = context.getApplicationContext().getPackageName();
    String link = "https://play.google.com/store/apps/details?id=" + packageName;

    sharingIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
    sharingIntent.putExtra(Intent.EXTRA_TEXT, link);
    context.startActivity(Intent.createChooser(sharingIntent, using));
  }

}
