import { Component, OnInit, inject, signal, computed } from "@angular/core";
import { Product } from "app/products/data-access/product.model";
import { ProductsService } from "app/products/data-access/products.service";
import { CartService } from "app/cart/cart.service";
import { ProductFormComponent } from "app/products/ui/product-form/product-form.component";
import { ButtonModule } from "primeng/button";
import { CardModule } from "primeng/card";
import { DataViewModule } from 'primeng/dataview';
import { DialogModule } from 'primeng/dialog';
import { RatingModule } from 'primeng/rating';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


const emptyProduct: Product = {
  id: 0,
  code: "",
  name: "",
  description: "",
  image: "",
  category: "",
  price: 0,
  quantity: 0,
  internalReference: "",
  shellId: 0,
  inventoryStatus: "INSTOCK",
  rating: 0,
  createdAt: 0,
  updatedAt: 0,
};

@Component({
  selector: "app-product-list",
  templateUrl: "./product-list.component.html",
  styleUrls: ["./product-list.component.scss"],
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    DataViewModule,
    CardModule,
    ButtonModule,
    DialogModule,
    RatingModule,
    ProductFormComponent
  ],
})
export class ProductListComponent implements OnInit {
  private readonly productsService = inject(ProductsService);
  public readonly cartService = inject(CartService);

  public readonly products = this.productsService.products;

  public isDialogVisible = false;
  public isCreation = false;
  public readonly editedProduct = signal<Product>(emptyProduct);

  public filterValue = signal<string>('');
  public rows = signal<number>(5);
  public first = signal<number>(0);

  public readonly filteredProducts = computed(() => {
    const allProducts = this.products();
    const filter = this.filterValue();

    return allProducts.filter(product =>
      product.name.toLowerCase().includes(filter.toLowerCase()) ||
      product.description.toLowerCase().includes(filter.toLowerCase())
    );
  });

  public readonly filteredAndPaginatedProducts = computed(() => {
    const filteredProducts = this.filteredProducts();
    const firstRow = this.first();
    const numRows = this.rows();

    const startIndex = firstRow;
    const endIndex = startIndex + numRows;
    return filteredProducts.slice(startIndex, endIndex);
  });

  public readonly totalFilteredProducts = computed(() => {
    return this.filteredProducts().length;
  });

  ngOnInit() {
    this.productsService.get().subscribe();
  }

  public onCreate() {
    this.isCreation = true;
    this.isDialogVisible = true;
    this.editedProduct.set(emptyProduct);
  }

  public onUpdate(product: Product) {
    this.isCreation = false;
    this.isDialogVisible = true;
    this.editedProduct.set(product);
  }

  public onDelete(product: Product) {
    this.productsService.delete(product.id).subscribe();
  }

  public onSave(product: Product) {
    if (this.isCreation) {
      this.productsService.create(product).subscribe();
    } else {
      this.productsService.update(product).subscribe();
    }
    this.closeDialog();
  }

  public onCancel() {
    this.closeDialog();
  }

  private closeDialog() {
    this.isDialogVisible = false;
  }

  public addToCart(product: Product) {
    this.cartService.addToCart(product, 1);
  }

  public decreaseQuantity(productId: number): void {
    const currentQuantity = this.cartService.getQuantityInCart(productId);
    if (currentQuantity > 1) {
      this.cartService.updateQuantity(productId, currentQuantity - 1);
    } else {
      this.cartService.removeFromCart(productId);
    }
  }

  public getStockStatusClass(status: string): string {
    switch (status) {
      case 'INSTOCK':
        return 'bg-green-100 text-green-800';
      case 'LOWSTOCK':
        return 'bg-yellow-100 text-yellow-800';
      case 'OUTOFSTOCK':
        return 'bg-red-100 text-red-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  }

  public getStockStatusLabel(status: string): string {
    switch (status) {
      case 'INSTOCK':
        return 'En stock';
      case 'LOWSTOCK':
        return 'Stock faible';
      case 'OUTOFSTOCK':
        return 'Rupture';
      default:
        return 'Inconnu';
    }
  }

  public onFilter(event: Event) {
    const inputElement = event.target as HTMLInputElement;
    this.filterValue.set(inputElement.value);
    this.first.set(0);
  }

  public onPage(event: any) {
    this.first.set(event.first);
    this.rows.set(event.rows);
  }
}
