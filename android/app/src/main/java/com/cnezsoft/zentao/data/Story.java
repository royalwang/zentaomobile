package com.cnezsoft.zentao.data;

import android.database.Cursor;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Catouse on 2015/2/2.
 */
public class Story extends DataEntry {
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

    public enum Status {
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
        return Enum.valueOf(Stage.class, getAsString(StoryColumn.stage).trim().toLowerCase());
    }

    /**
     * Get close reason
     * @return
     */
    public CloseReason getCloseReason() {
        return Enum.valueOf(CloseReason.class, getAsString(StoryColumn.closedReason).trim().toLowerCase());
    }

    /**
     * Get story status
     * @return
     */
    public Story.Status getStatus() {
        return Enum.valueOf(Story.Status.class, getAsString(StoryColumn.status).trim().toLowerCase());
    }

    /**
     * Get story srouce
     * @return
     */
    public Source getSource() {
        return Enum.valueOf(Story.Source.class, getAsString(StoryColumn.source).trim().toLowerCase());
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
