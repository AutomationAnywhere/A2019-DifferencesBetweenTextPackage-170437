/*
 * Copyright (c) 2019 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */
/**
 *
 */
package com.automationanywhere.botcommand.samples.commands.basic;

import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.ListValue;
import com.automationanywhere.botcommand.data.impl.RecordValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.data.model.Schema;
import com.automationanywhere.botcommand.data.model.record.Record;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.CommandPkg;
import com.automationanywhere.commandsdk.annotations.Execute;
import com.automationanywhere.commandsdk.annotations.Idx;
import com.automationanywhere.commandsdk.annotations.Pkg;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.i18n.Messages;
import com.automationanywhere.commandsdk.i18n.MessagesFactory;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.automationanywhere.commandsdk.model.AttributeType;
import com.automationanywhere.commandsdk.model.DataType;
import com.automationanywhere.toolchain.runtime.util.ValueConverter;
import difflib.Chunk;
import difflib.Delta;
import difflib.Delta.TYPE;
import difflib.DiffUtils;
import difflib.Patch;

/**
 */

//BotCommand makes a class eligible for being considered as an action.
@BotCommand

//CommandPks adds required information to be dispalable on GUI.
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "String_Diff", label = "[[StringDiff.label]]",
        node_label = "[[StringDiff.node_label]]", description = "[[StringDiff.description]]", icon = "pkg.svg",

        //Return type information. return_type ensures only the right kind of variable is provided on the UI.
        return_label = "[[StringDiff.return_label]]", return_type = DataType.LIST, return_sub_type = STRING, return_required = true)
public class DiffString {

    //Messages read from full qualified property file name and provide i18n capability.
    private static final Messages MESSAGES = MessagesFactory
            .getMessages("com.automationanywhere.botcommand.samples.messages");

    //Identify the entry point for the action. Returns a Value<String> because the return type is String.
    @Execute
    public Value<List> action(
            //Idx 1 would be displayed first, with a text box for entering the value.
//Isono
            @Idx(index = "1", type = AttributeType.LIST)
            @Pkg(label = "[[StringDiff.firstString.label]]")
            @NotEmpty
                    List<Value> sourceList,
            @Idx(index = "2", type = AttributeType.LIST)
            @Pkg(label = "[[StringDiff.secondString.label]]")
            @NotEmpty
                    List<Value> targetList
//Isono
            ) {

        //Internal validation, to disallow empty strings. No null check needed as we have NotEmpty on firstString.
        if (sourceList.isEmpty())
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "firstString"));

        if (targetList.isEmpty())
            throw new BotCommandException(MESSAGES.getString("emptyInputString", "secondString"));

        //Business logic
//        List<String> l_first = new ArrayList<String>(sourceList);
        List<String> l_first = new ArrayList<String>();
        //sourceList.forEach(e->l_first.add(e.toString()));
        //sourceList.forEach(e->l_first.add(e.get().toString()));
        for (int i=0 ; i<sourceList.size(); i++){
            String str = new StringValue(sourceList.get(i)).get();
            l_first.add(str );
        }

        List<String> l_second = new ArrayList<String>();
        //targetList.forEach(e->l_second.add(e.toString()));
        //targetList.forEach(e->l_second.add(e.get().toString()));
        for (int i=0 ; i<targetList.size(); i++){
            String str = new StringValue(targetList.get(i)).get();
            l_second.add(str );
        }


        Patch<String> diffs = DiffUtils.diff(l_first,l_second);
//Isono Modify start 20200523
        //String result = diffs.getDeltas().toString();

        //Return StringValue.
        //return new StringValue(result);

//Isono Modify 20200523

        //Return list of AA record object
        List<StringValue> l_result = new ArrayList<StringValue>();

        List<Delta<String>> deltas = diffs.getDeltas();
        for (Delta<String> delta : deltas) {
            TYPE type = delta.getType();
            Chunk<String> source_c = delta.getOriginal();
            int source_pos = source_c.getPosition();
            String source_str = source_c.getLines().toString();

            Chunk<String> target_c = delta.getRevised();
            int target_pos = target_c.getPosition();
            String target_str = target_c.getLines().toString();

            //Add to List
            StringValue strTemp = new StringValue();
            strTemp.set( type.name()
                    + " | Line:" + source_pos
                    + " | From:" + source_str
                    + " | Line:" + target_pos
                    + " | To:" + target_str);
            l_result.add(strTemp);
        }
        ListValue lv = new ListValue();
        lv.set(l_result);
        return lv;

//Isono Modify End 20200523


    }
}
