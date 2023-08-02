import { Component, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ModelService } from 'src/shared/services/model.service';
import { ProductService } from '../../shared/services/product.service';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss']
})
export class StockComponent implements OnInit {

  products: any[] = [];
  productsClone: any[] = [];
  models: any[] = [];
  cols: any[] = [];
  selectedProducts: any[] = [];
  oldProduct: any;
  @ViewChild('dt') dt!: Table;
  constructor(private productService: ProductService, private modelService: ModelService, private messageService: MessageService,private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    console.log(' ngOnInit Stock');

    this.productService.findAllProducts()
    .subscribe((result: any) => {
      this.products = result;
      console.log('this.products',this.products);

      this.productsClone = [...this.products]
    });

    this.modelService.findAllModels()
    .subscribe((result: any) => {
      console.log('models',result);
      this.models = result;
    });
    this.cols = [
      //{ field: 'model', header: 'Modèle' },
      //{ field: 'id', header: 'Id' },
      { field: 'color.name', header: 'Couleur' },
      { field: 'size.reference', header: 'Taille' },
      { field: 'quantity', header: 'Quantité'},
      { field: 'reference', header: 'Référence' },
      { field: 'date', header: 'Dernière modification' }
    ];
  }

  onEditInit($event: any){
    this.oldProduct = Object.assign({}, $event.data);
  }

  getProducts(idModel: any){
    const filteredProducts = this.productsClone.filter(product => Number(product.model.id) === Number(idModel));
    this.products = filteredProducts;
    //this.products = [...this.products]
    console.log('this.products by model',filteredProducts,'idModel',idModel);
  }

  onEditComplete($event: any) {
    if(this.oldProduct.quantity != $event.data.quantity) {
      console.log($event.data);
      $event.data.date = new Date();
        this.productService.updatProduct($event.data)
        .subscribe(result => {
          console.log("quantity successfully updated !");
          this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'La qunatité a été modifié avec succés', life: 1000 });
        })
    }
  }

/*   search($event: any){
      this.dt.filterGlobal($event.target.value, 'contains');
/*  //this.models = this.modelsClone.filter(model => model.name.includes($event.target.value));
      this.products = this.productsClone.filter(product => (product.reference.includes($event.target.value)
     || product.color.name.includes($event.target.value) || product.size.reference.includes($event.target.value))).slice();
  }
 */
  onEditCancel($event: any){

  }

  deleteSelectedProducts() {
    let selectedProductsId = this.selectedProducts.map((selectedProduct: any) => selectedProduct.id);
    console.log(selectedProductsId);

    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les produits séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.productService.deleteSelectedProducts(selectedProductsId)
          .subscribe((result: any) => {
            console.log("products successfully deleted !");
            this.products = this.products.filter((product: any) => selectedProductsId.indexOf(product.id) == -1);
            this.selectedProducts = [];
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les produits séléctionnés ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }

  getColor(qte: any) : any{
    if (qte <= 3 && qte > 0)
    return '.warning-product';
    if(qte == 0)
    return '.no-products';
  }
}
