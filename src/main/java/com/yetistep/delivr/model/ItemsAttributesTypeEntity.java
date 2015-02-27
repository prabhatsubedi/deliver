package com.yetistep.delivr.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sagar
 * Date: 12/18/14
 * Time: 5:21 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity(name = "ItemsAttributesTypes")
@Table(name = "items_attributes_types")
public class ItemsAttributesTypeEntity {

   private Integer id;
   private String type;
   private Boolean multiSelect;
   private ItemEntity item;
   private List<ItemsAttributeEntity> itemsAttribute;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "item_id")
    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    @OneToMany(mappedBy = "type", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    public List<ItemsAttributeEntity> getItemsAttribute() {
        return itemsAttribute;
    }

    public void setItemsAttribute(List<ItemsAttributeEntity> itemsAttribute) {
        this.itemsAttribute = itemsAttribute;
    }

    @Column(name = "type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Column(name = "multi_select", columnDefinition = "TINYINT(1)")
    public Boolean getMultiSelect() {
        return multiSelect;
    }

    public void setMultiSelect(Boolean multiSelect) {
        this.multiSelect = multiSelect;
    }
}
