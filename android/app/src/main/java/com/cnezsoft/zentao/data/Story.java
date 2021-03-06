package com.cnezsoft.zentao.data;

import android.content.Context;
import android.database.Cursor;

import com.cnezsoft.zentao.IAccentIcon;
import com.cnezsoft.zentao.ZentaoApplication;
import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Story
 * Created by Catouse on 2015/2/2.
 */
public class Story extends DataEntry {

    /**
     * Story sources
     */
    public enum Source {
        _,
        customer,
        user,
        po,
        market,
        service,
        competitor,
        partner,
        dev,
        tester,
        bug,
        other
    }

    /**
     * Story stage
     */
    public enum Stage {
        _,
        wait,
        planned,
        projected,
        developing,
        developed,
        testing,
        tested,
        verified,
        released
    }

    /**
     * Story closeReasons
     */
    public enum CloseReason {
        _,
        done,
        subdivided,
        duplicate,
        postponed,
        willnotdo,
        cancel,
        bydesign
    }

    /**
     * Story status
     */
    public enum Status implements IAccentIcon {
        _(MaterialColorSwatch.Grey, "question"),
        draft(MaterialColorSwatch.Purple, "pencil"),
        active(MaterialColorSwatch.Brown, "flag"),
        closed(MaterialColorSwatch.Grey, "dot-circle-o"),
        changed(MaterialColorSwatch.Red, "random");

        Status(MaterialColorSwatch accentColor, String iconName) {
            this.accentColor = accentColor;
            this.iconName = iconName;
        }

        private MaterialColorSwatch accentColor;

        public MaterialColorSwatch accent() {
            return accentColor;
        }

        private String iconName;

        public String icon() {
            return iconName;
        }
    }

    /**
     * Get story stage
     * @return
     */
    public Stage getStage() {
        try {
            return Enum.valueOf(Stage.class, getAsString(StoryColumn.stage).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Stage._;
        }
    }

    /**
     * Get close reason
     * @return
     */
    public CloseReason getCloseReason() {
        try {
            return Enum.valueOf(CloseReason.class, getAsString(StoryColumn.closedReason).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return CloseReason._;
        }
    }

    /**
     * Get story status
     * @return
     */
    public Story.Status getStatus() {
        try {
            return Enum.valueOf(Story.Status.class, getAsString(StoryColumn.status).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Status._;
        }
    }

    /**
     * Get story srouce
     * @return
     */
    public Source getSource() {
        try {
            return Enum.valueOf(Story.Source.class, getAsString(StoryColumn.source).trim().toLowerCase());
        } catch (IllegalArgumentException e) {
            return Source._;
        }
    }

    public enum PageTab implements IPageTab {
        assignedTo,
        openedBy,
        reviewedBy;

        @Override
        public String text(Context context) {
            return ZentaoApplication.getEnumText(context, this);
        }

        @Override
        public PageTab[] tabs() {
            return values();
        }

        @Override
        public EntryType getEntryType() {
            return EntryType.Story;
        }
    }

    /**
     * Constructor without params
     */
    public Story() {}

    /**
     * Constructor with JSONObject
     */
    public Story(JSONArray jsonArray, String[] keys) {
        super(jsonArray, keys);
    }

    /**
     * Constructor with json
     *
     * @param json
     */
    public Story(JSONObject json) {
        super(json);
    }

    /**
     * Constructor with cursor
     *
     * @param cursor
     */
    public Story(Cursor cursor) {
        super(cursor);
    }

    /**
     * The method for inherited classes to init settings
     */
    @Override
    protected void onCreate() {
        setType(EntryType.Story);
    }
}
