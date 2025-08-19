import { Component, inject } from "@angular/core";
import { RouterModule } from "@angular/router";
import { SplitterModule } from 'primeng/splitter';
import { ToolbarModule } from 'primeng/toolbar';
import { ButtonModule } from 'primeng/button';
import { SidebarModule } from 'primeng/sidebar';
import { PanelMenuComponent } from "./shared/ui/panel-menu/panel-menu.component";
import { CartService } from "./cart/cart.service";
import { CartComponent } from "./shared/ui/cart/cart.component";

@Component({
  selector: "app-root",
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.scss"],
  standalone: true,
  imports: [
    RouterModule,
    SplitterModule,
    ToolbarModule,
    ButtonModule,
    SidebarModule,
    PanelMenuComponent,
    CartComponent
  ],
})
export class AppComponent {
  title = "ALTEN SHOP";
  public readonly cartService = inject(CartService);
  public cartVisible = false;

  public toggleCart(): void {
    this.cartVisible = !this.cartVisible;
  }
}
