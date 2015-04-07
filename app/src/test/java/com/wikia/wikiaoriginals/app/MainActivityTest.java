package com.wikia.wikiaoriginals.app;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.request.SpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.wikia.wikiaoriginals.service.model.Wiki;
import com.wikia.wikiaoriginals.service.requests.RequestPopularWikis;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.ActivityController;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = "src/main/AndroidManifest.xml", emulateSdk = 18)
public class MainActivityTest {
    private MainActivity activity;
    private ActivityController activityController;

    @Before
    public void setup()  {
        activityController = Robolectric.buildActivity(MainActivity.class);
        activity = (MainActivity)activityController.get();
    }

    @Test
    public void activityShouldNotNull() throws Exception {
        assertNotNull(activity);
    }

    @Test
    public void listItemSizeShouldBeTwoWhenActivityIsResumed(){
        final SpiceManager spiceManager = mock(SpiceManager.class);
        activity.spiceManager = spiceManager;

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                final RequestListener requestListener = (RequestListener)invocation.getArguments()[1];

                final List<Wiki> wikis = new ArrayList<>();
                wikis.add(new Wiki(5, "Test", "Test", "Test"));
                wikis.add(new Wiki(6, "Test", "Test", "Test"));

                final RequestPopularWikis.PopularWikis popularWikis = new RequestPopularWikis.PopularWikis(wikis, 1, 10);
                requestListener.onRequestSuccess(popularWikis);
                return null;
            }
        }).when(spiceManager).execute(any(SpiceRequest.class), any(RequestListener.class));

        activityController.create().start().resume();

        assertNotNull(activity.wikiListAdapter);
        assertEquals(activity.wikiListAdapter.getData().getWikis().size(), 2);
    }
}
