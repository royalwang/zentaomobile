package com.cnezsoft.zentao;

import android.os.Bundle;
import android.widget.TextView;

import com.cnezsoft.zentao.colorswatch.MaterialColorSwatch;
import com.cnezsoft.zentao.control.ControlBindInfo;
import com.cnezsoft.zentao.data.DAO;
import com.cnezsoft.zentao.data.DataEntry;
import com.cnezsoft.zentao.data.EntryType;
import com.cnezsoft.zentao.data.Product;
import com.cnezsoft.zentao.data.ProductColumn;

/**
 * Product detail activity
 * Created by sunhao on 15/3/3.
 */
public class ProductDetailActivity extends DetailActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAccentSwatch(EntryType.Product.accent());
    }

    @Override
    protected boolean setIcon(MaterialColorSwatch swatch, TextView iconView, TextView iconBackView, TextView iconTextView) {
        super.setIcon(swatch, iconView, iconBackView, iconTextView);
        iconBackView.setTextColor(Product.accent(entryId).primary().value());
        return true;
    }

    @Override
    protected DataEntry loadData() {
        super.loadData();
        Product product = (Product) entry;
        DAO dao = getDAO();
        String key = product.key();
        product.setStoryCount(dao);
        product.setBugCount(dao.getBugCountOfProduct(key));
        return product;
    }

    @Override
    protected void display(DataEntry dataEntry) {
        Product product = (Product) dataEntry;
        Product.Status status = product.getStatus();

        displayTitle(product.getAsString(ProductColumn.name));
        displayId("#" + product.key());
        displayOnTextview(R.id.text_info, getString(R.string.text_product_code) + " " + product.getAsString(ProductColumn.code));

        displayStatus(status, new ControlBindInfo(Helper.formatDate(product.getAsDate(ProductColumn.createdDate), getString(R.string.text_create_at_format))));
        if(product.getBugCount() > 0) {
            displayMeta(getString(R.string.text_project_active_bug), product.getBugCount(), "{fa-bug}");
        }
        displayMeta(getString(R.string.text_product_stories), String.format(getString(R.string.text_product_stories_format),
                product.getStoryCount(),
                product.getChangedCount(),
                product.getDraftCount(),
                product.getCloseCount()), "{fa-" + EntryType.Story.icon() + "}");

        displayMeta(ZentaoApplication.getEnumText(this, ProductColumn.desc), product.getFriendlyString(ProductColumn.desc), "{fa-file-text-o}");

        Product.Acl acl = product.getAcl();
        displayMeta(ZentaoApplication.getEnumText(this, ProductColumn.acl), ZentaoApplication.getEnumText(this, acl), acl == Product.Acl.open ? "{fa-unlock}" : "{fa-lock}");
        displayMeta(ZentaoApplication.getEnumText(this, ProductColumn.createdBy), product.getAsString(ProductColumn.createdBy), "{fa-user}");
        displayMeta(ZentaoApplication.getEnumText(this, ProductColumn.PO), product.getAsString(ProductColumn.PO), false);
        displayMeta(ZentaoApplication.getEnumText(this, ProductColumn.QD), product.getAsString(ProductColumn.QD), false);
        displayMeta(ZentaoApplication.getEnumText(this, ProductColumn.RD), product.getAsString(ProductColumn.RD), false);
        String whiteList = product.getAsString(ProductColumn.whitelist);
        if(!Helper.isNullOrEmpty(whiteList)) {
            displayMeta(ZentaoApplication.getEnumText(this, ProductColumn.whitelist), whiteList, "{fa-group}");
        }
    }
}
