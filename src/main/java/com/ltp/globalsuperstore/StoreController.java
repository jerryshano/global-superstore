package com.ltp.globalsuperstore;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.DataInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class StoreController {

    private List<Item> itemsList = new ArrayList<>();

    public void print(){
        System.out.println(Constants.CATEGORIES[0]);
    }

    @GetMapping("/")
    public String getForm(Model model, @RequestParam(required = false) String id) {
        System.out.println("id: " + id);
        int index = getIndexFromId(id);
        Item formItem;
        if (index == Constants.NOT_FOUND) {
            formItem = new Item();
        } else {
            formItem = itemsList.get(index);
        }
        model.addAttribute("formItemAtt", formItem);
        model.addAttribute("categoriesAtt", Constants.CATEGORIES);
        return "form";
    }

    @PostMapping("/submitItem")
    public String handleSubmit(Item item, RedirectAttributes redirectAttributes) {
        int index = getIndexFromId(item.getId());
        String status = Constants.SUCCESS_STATUS;
        if (index == Constants.NOT_FOUND) {
            itemsList.add(item);
        } else if (within5Days(item.getDate(), itemsList.get(index).getDate())){
            itemsList.set(index, item);
        } else {
            status = Constants.FAILED_STATUS;
        }
        redirectAttributes.addFlashAttribute("status", status);
        return "redirect:/inventory";
    }

    @GetMapping("/inventory")
    public String getInventory(Model model) {
        model.addAttribute("itemsListAtt", itemsList);
        return "inventory";
    }

    public int getIndexFromId(String id) {
        for (int i = 0; i < itemsList.size(); i++) {
            if (itemsList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return Constants.NOT_FOUND;
    }

    public boolean within5Days(Date newDate, Date oldDate) {
        long diff = Math.abs(newDate.getTime() - oldDate.getTime());
        System.out.println((TimeUnit.MILLISECONDS.toDays(diff)) <= 5);
        return (int) (TimeUnit.MILLISECONDS.toDays(diff)) <= 5;
    }
}
