import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';

interface Cidade {
  id: number;
  nome: string;
  comercios: Comercio[];
}

interface Comercio {
  id: number;
  nome: string;
  responsavel: string;
  tipo: string;
  cidadeId: number;
}

@Component({
  selector: 'app-cidade',
  templateUrl: './cidade.component.html',
  styleUrls: ['./cidade.component.css'],
  providers: [ConfirmationService, MessageService]
})
export class CidadeComponent implements OnInit {
  cidades: Cidade[] = [];
  novaCidade: Cidade = { id: 0, nome: '', comercios: [] };
  editando: boolean = false;

  constructor(private http: HttpClient, private confirmationService: ConfirmationService, private messageService: MessageService) {}

  ngOnInit(): void {
    this.carregarCidades();
  }

  carregarCidades() {
    this.http.get<Cidade[]>('http://localhost:8080/cidades')
      .subscribe(dados => this.cidades = dados);
  }

  adicionarOuEditarCidade() {
    if (this.editando) {
      this.http.put<Cidade>(`http://localhost:8080/cidades/${this.novaCidade.id}`, this.novaCidade)
        .subscribe(() => {
          this.messageService.add({severity:'success', summary:'Sucesso', detail:'Cidade atualizada!'});
          this.resetarFormulario();
        });
    } else {
      this.http.post<Cidade>('http://localhost:8080/cidades', this.novaCidade)
        .subscribe(() => {
          this.messageService.add({severity:'success', summary:'Sucesso', detail:'Cidade adicionada!'});
          this.resetarFormulario();
        });
    }
  }

  editarCidade(cidade: Cidade) {
    this.novaCidade = { ...cidade };
    this.editando = true;
  }

  excluirCidade(id: number) {
    this.confirmationService.confirm({
      message: 'Tem certeza que deseja excluir esta cidade?',
      accept: () => {
        this.http.delete(`http://localhost:8080/cidades/${id}`)
          .subscribe(() => {
            this.messageService.add({severity:'warn', summary:'Removido', detail:'Cidade excluída!'});
            this.carregarCidades();
          });
      }
    });
  }

  resetarFormulario() {
    this.novaCidade = { id: 0, nome: '', comercios: [] };
    this.editando = false;
    this.carregarCidades();
  }
}
