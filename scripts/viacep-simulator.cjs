
// Apenas para testes de resiliência; nunca é usado por padrão pela aplicação.
const http = require('node:http');
const server = http.createServer((req,res)=>{
  res.setHeader('Content-Type','application/json');
  const cep = req.url.match(/^\/ws\/([0-9]{8})\/json\/$/)?.[1];
  if(cep==='11111111'){res.writeHead(500);return res.end('{}');}
  if(cep==='22222222'){const timer=setTimeout(()=>res.end('{"localidade":"Mogi das Cruzes"}'),6000); res.on('close',()=>clearTimeout(timer)); return;}
  if(cep==='33333333') return res.end('not-json');
  if(cep==='44444444') return res.end('{}');
  if(cep==='55555555') return req.socket.destroy();
  if(cep==='08773380') return res.end('{"localidade":"Mogi das Cruzes"}');
  res.end('{"erro":true}');
});
server.listen(8099,'127.0.0.1',()=>console.log('Simulador ViaCEP: http://127.0.0.1:8099/ws. Encerre com Ctrl+C.'));
