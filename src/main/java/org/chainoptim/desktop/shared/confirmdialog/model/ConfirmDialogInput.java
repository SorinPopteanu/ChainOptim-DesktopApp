package org.chainoptim.desktop.shared.confirmdialog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmDialogInput {

    private String dialogTitle;
    private String dialogMessage;
    private String dialogExtraChildViewPath;
}
