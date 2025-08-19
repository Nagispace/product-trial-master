import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CartService } from 'app/cart/cart.service';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { InputNumberModule } from 'primeng/inputnumber';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, ButtonModule, CardModule, InputNumberModule, FormsModule],
  templateUrl: './cart.component.html'
})
export class CartComponent {
  public readonly cartService = inject(CartService);

  public increaseQuantity(productId: number): void {
    const currentQuantity = this.cartService.getQuantityInCart(productId);
    this.cartService.updateQuantity(productId, currentQuantity + 1);
  }

  public decreaseQuantity(productId: number): void {
    const currentQuantity = this.cartService.getQuantityInCart(productId);
    if (currentQuantity > 1) {
      this.cartService.updateQuantity(productId, currentQuantity - 1);
    } else {
      this.cartService.removeFromCart(productId);
    }
  }

  public removeItem(productId: number): void {
    this.cartService.removeFromCart(productId);
  }

  public clearCart(): void {
    this.cartService.clearCart();
  }

  public checkout(): void {
    alert('Fonctionnalité de commande à implémenter !');
  }
}
