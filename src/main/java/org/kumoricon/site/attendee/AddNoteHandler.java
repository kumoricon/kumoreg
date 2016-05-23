package org.kumoricon.site.attendee;


import org.kumoricon.site.attendee.window.AddNoteWindow;

public interface AddNoteHandler {
    void addNote(AddNoteWindow window, String message);
    void addNoteCancel(AddNoteWindow window);
}
