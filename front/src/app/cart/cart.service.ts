import { Injectable, signal, computed } from "@angular/core";
import { Product } from "../products/data-access/product.model";

export interface CartItem {
  product: Product;
  quantity: number;
}

@Injectable({
  providedIn: "root"
})
export class CartService {
  private readonly _cartItems = signal<CartItem[]>([]);

  public readonly cartItems = this._cartItems.asReadonly();

  public readonly totalItems = computed(() =>
    this._cartItems().reduce((total, item) => total + item.quantity, 0)
  );

  public readonly totalPrice = computed(() =>
    this._cartItems().reduce((total, item) => total + (item.product.price * item.quantity), 0)
  );

  /**
   * Ajoute un produit au panier
   */
  public addToCart(product: Product, quantity: number = 1): void {
    this._cartItems.update(items => {
      const existingItemIndex = items.findIndex(item => item.product.id === product.id);

      if (existingItemIndex >= 0) {
        const updatedItems = [...items];
        updatedItems[existingItemIndex] = {
          ...updatedItems[existingItemIndex],
          quantity: updatedItems[existingItemIndex].quantity + quantity
        };
        return updatedItems;
      } else {
        return [...items, { product, quantity }];
      }
    });
  }

  /**
   * Supprime un produit du panier
   */
  public removeFromCart(productId: number): void {
    this._cartItems.update(items =>
      items.filter(item => item.product.id !== productId)
    );
  }

  /**
   * Met à jour la quantité d'un produit dans le panier
   */
  public updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }

    this._cartItems.update(items => {
      const itemIndex = items.findIndex(item => item.product.id === productId);
      if (itemIndex >= 0) {
        const updatedItems = [...items];
        updatedItems[itemIndex] = {
          ...updatedItems[itemIndex],
          quantity
        };
        return updatedItems;
      }
      return items;
    });
  }

  /**
   * Vide complètement le panier
   */
  public clearCart(): void {
    this._cartItems.set([]);
  }

  /**
   * Vérifie si un produit est dans le panier
   */
  public isInCart(productId: number): boolean {
    return this._cartItems().some(item => item.product.id === productId);
  }

  /**
   * Récupère la quantité d'un produit dans le panier
   */
  public getQuantityInCart(productId: number): number {
    const item = this._cartItems().find(item => item.product.id === productId);
    return item ? item.quantity : 0;
  }
}
